//package com.amnex.agristack.entity;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import lombok.EqualsAndHashCode;
//import org.hibernate.annotations.GenericGenerator;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import java.util.Calendar;
//
//
//@Entity
//@EqualsAndHashCode(callSuper = false)
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
//public class IdentifierType extends BaseEntity{
//    /**
//     * type: integer
//     * field: id
//     */
//    @Id
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
//    @Column(name = "id", nullable = false, columnDefinition="Integer")
//    private String id;
//
//    /**
//     * type: String
//     * field: identifier code
//     */
//    @Column(name = "identifier_code", columnDefinition="VARCHAR(6)")
//    private String identifierCode;
//
//    /**
//     * type: string
//     * field: identifier description
//     */
//    @Column(name = "identifier_description", columnDefinition="VARCHAR(100)")
//    private String identifierDescription;
//
//    /**
//     * get Id
//     * @return id
//     */
//    public String getId() {
//        return id;
//    }
//
//    /**
//     * set Id
//     * @param id
//     */
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    /**
//     * get identifier code
//     * @return identifierCode
//     */
//    public String getIdentifierCode() {
//        return identifierCode;
//    }
//
//    /**
//     * set identifier code
//     * @param identifierCode
//     */
//
//    public void setIdentifierCode(String identifierCode) {
//        this.identifierCode = identifierCode;
//    }
//
//    /**
//     * get identifier description
//     * @return identifierDescription
//     */
//    public String getIdentifierDescription() {
//        return identifierDescription;
//    }
//
//    /**
//     * set identifier description
//     * @param identifierDescription
//     */
//    public void setIdentifierDescription(String identifierDescription) {
//        this.identifierDescription = identifierDescription;
//    }
//
//}
