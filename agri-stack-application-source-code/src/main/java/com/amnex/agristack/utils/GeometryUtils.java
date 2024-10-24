package com.amnex.agristack.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.amnex.agristack.dao.CadastralLinePointDTO;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import com.vividsolutions.jts.operation.distance.DistanceOp;

@Service
public class GeometryUtils {

	public List<LineString> extractLineStringsFromGeometry(Geometry geometry) {
		List<LineString> lineStrings = new ArrayList<>();

		if (geometry instanceof Polygon) {
			Polygon polygon = (Polygon) geometry;

			return extractSidesFromPolygon(polygon);
//			lineStrings.add(polygon.getExteriorRing());
//
//			for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
//				lineStrings.add(polygon.getInteriorRingN(i));
//			}
		} else if (geometry instanceof GeometryCollection) {
			GeometryCollection collection = (GeometryCollection) geometry;
			for (int i = 0; i < collection.getNumGeometries(); i++) {
				Geometry subGeometry = collection.getGeometryN(i);
				lineStrings.addAll(extractLineStringsFromGeometry(subGeometry));
			}
		} else if (geometry instanceof LineString) {
			lineStrings.add((LineString) geometry);
		}

		return lineStrings;
	}

	public List<LineString> extractSidesFromPolygon(Polygon polygon) {
		List<LineString> sides = new ArrayList<>();

		// Add exterior ring sides
		CoordinateSequence exteriorCoords = polygon.getExteriorRing().getCoordinateSequence();
		addRingSides(exteriorCoords, sides);

		// Add interior ring sides (holes)
		for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
			CoordinateSequence holeCoords = polygon.getInteriorRingN(i).getCoordinateSequence();
			addRingSides(holeCoords, sides);
		}

		return sides;
	}

	private void addRingSides(CoordinateSequence ringCoords, List<LineString> sides) {
		int numPoints = ringCoords.size();
		for (int i = 1; i < numPoints; i++) {
			Coordinate p1 = ringCoords.getCoordinate(i - 1);
			Coordinate p2 = ringCoords.getCoordinate(i);
			LineString side = new GeometryFactory().createLineString(new Coordinate[] { p1, p2 });
			sides.add(side);
		}
	}

	public Point findPerpendicularPoint(LineString lineString, Point point) {
		// Find the closest point on the LineString to the given point
		LengthIndexedLine indexedLine = new LengthIndexedLine(lineString);
		double index = indexedLine.project(point.getCoordinate());
		Coordinate closestCoordinate = indexedLine.extractPoint(index);

		// Calculate the perpendicular distance between the closest point and the given
		// point
		double distance = closestCoordinate.distance(point.getCoordinate());

		// Move along the LineString based on the calculated distance to find the
		// perpendicular point
		double newLineIndex = index + distance;
		Coordinate newCoordinate = indexedLine.extractPoint(newLineIndex);

		// Create the perpendicular point
		return point.getFactory().createPoint(newCoordinate);
	}

	public Point findClosestPoint(LineString lineString, Point point) {
		DistanceOp distanceOp = new DistanceOp(lineString, point);
		Coordinate closestCoordinate = distanceOp.nearestPoints()[distanceOp.nearestPoints().length - 2];
		GeometryFactory geometryFactory = new GeometryFactory();
		return geometryFactory.createPoint(closestCoordinate);
	}

	public CadastralLinePointDTO findPerpendicularPointFromLatLong(Geometry geom, Double lat, Double lon) {
		List<LineString> lineStrings = extractLineStringsFromGeometry(geom);

		GeometryFactory geometryFactory = new GeometryFactory();
		Coordinate coordinate = new Coordinate(lon, lat);
		Point point = geometryFactory.createPoint(coordinate);

		List<CadastralLinePointDTO> linePointDTOs = new ArrayList<>();

		lineStrings.forEach((x) -> {

			Point perpPoint = findClosestPoint(x, point);

			CadastralLinePointDTO dto = new CadastralLinePointDTO();
			dto.setLineStringGeom(x);
			dto.setPointGeom(point);
			dto.setPerpPointGeom(perpPoint);

			Double distance = point.distance(perpPoint);
			Double distanceInMtr = distance / 180 * 3.14 * 6371 * 1000;
			dto.setNearestDistance(distanceInMtr);
			linePointDTOs.add(dto);

		});

		Optional<CadastralLinePointDTO> optionalDto = linePointDTOs.stream()
				.min(Comparator.comparingDouble(CadastralLinePointDTO::getNearestDistance));

		return optionalDto.isPresent() ? optionalDto.get() : null;
	}
}
