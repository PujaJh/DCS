/**
 * 
 */
package com.amnex.agristack.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.Policy;
import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.CopyWriter;
import com.google.cloud.storage.ServiceAccount;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageBatch;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Acl.Entity;
import com.google.cloud.storage.Storage.BlobGetOption;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.Storage.BlobWriteOption;
import com.google.cloud.storage.Storage.BucketGetOption;
import com.google.cloud.storage.Storage.BucketListOption;
import com.google.cloud.storage.Storage.BucketSourceOption;
import com.google.cloud.storage.Storage.BucketTargetOption;
import com.google.cloud.storage.Storage.ComposeRequest;
import com.google.cloud.storage.Storage.CopyRequest;
import com.google.cloud.storage.Storage.SignUrlOption;

/**
 * @author majid.belim
 *
 */
@Configuration
public class GoogleCloudStorageConfig {
	@Value("${spring.cloud.gcp.credentials.location}")
	private String location;
	
	
	@Value("${app.bucket.enabled}")
	private Boolean enabled;
	
	@Bean
	public Storage storage() throws FileNotFoundException, IOException {
		// Replace "path/to/your/service-account-key.json" with the actual path to your
		// service account key JSON file.
//		new FileInputStream("s-0-000236-02-36110e497ce7.json")
		if(enabled.equals(true)) {
			
		
		StorageOptions options = StorageOptions.newBuilder().setCredentials(
				ServiceAccountCredentials.fromStream(new ClassPathResource(location).getInputStream()))
				.build();
		return options.getService();
		}
		else {
			
			return new Storage() {
				
				@Override
				public StorageOptions getOptions() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public WriteChannel writer(BlobInfo blobInfo, BlobWriteOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Acl updateDefaultAcl(String bucket, Acl acl) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Acl updateAcl(String bucket, Acl acl, BucketSourceOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Acl updateAcl(BlobId blob, Acl acl) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Acl updateAcl(String bucket, Acl acl) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Blob update(BlobInfo blobInfo, BlobTargetOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Bucket update(BucketInfo bucketInfo, BucketTargetOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public List<Blob> update(Iterable<BlobInfo> blobInfos) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public List<Blob> update(BlobInfo... blobInfos) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Blob update(BlobInfo blobInfo) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public List<Boolean> testIamPermissions(String bucket, List<String> permissions, BucketSourceOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public URL signUrl(BlobInfo blobInfo, long duration, TimeUnit unit, SignUrlOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Policy setIamPolicy(String bucket, Policy policy, BucketSourceOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public ReadChannel reader(String bucket, String blob, BlobSourceOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public ReadChannel reader(BlobId blob, BlobSourceOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public byte[] readAllBytes(String bucket, String blob, BlobSourceOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public byte[] readAllBytes(BlobId blob, BlobSourceOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public List<Acl> listDefaultAcls(String bucket) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public List<Acl> listAcls(String bucket, BucketSourceOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public List<Acl> listAcls(BlobId blob) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public List<Acl> listAcls(String bucket) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Page<Blob> list(String bucket, BlobListOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Page<Bucket> list(BucketListOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public ServiceAccount getServiceAccount(String projectId) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Policy getIamPolicy(String bucket, BucketSourceOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Acl getDefaultAcl(String bucket, Entity entity) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Acl getAcl(String bucket, Entity entity, BucketSourceOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Acl getAcl(BlobId blob, Entity entity) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Acl getAcl(String bucket, Entity entity) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Blob get(String bucket, String blob, BlobGetOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Blob get(BlobId blob, BlobGetOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Bucket get(String bucket, BucketGetOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public List<Blob> get(Iterable<BlobId> blobIds) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public List<Blob> get(BlobId... blobIds) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Blob get(BlobId blob) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public boolean deleteDefaultAcl(String bucket, Entity entity) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean deleteAcl(String bucket, Entity entity, BucketSourceOption... options) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean deleteAcl(BlobId blob, Entity entity) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean deleteAcl(String bucket, Entity entity) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean delete(String bucket, String blob, BlobSourceOption... options) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean delete(BlobId blob, BlobSourceOption... options) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean delete(String bucket, BucketSourceOption... options) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public List<Boolean> delete(Iterable<BlobId> blobIds) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public List<Boolean> delete(BlobId... blobIds) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public boolean delete(BlobId blob) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public Acl createDefaultAcl(String bucket, Acl acl) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Acl createAcl(String bucket, Acl acl, BucketSourceOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Acl createAcl(BlobId blob, Acl acl) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Acl createAcl(String bucket, Acl acl) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Blob create(BlobInfo blobInfo, InputStream content, BlobWriteOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Blob create(BlobInfo blobInfo, byte[] content, BlobTargetOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Blob create(BlobInfo blobInfo, BlobTargetOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Bucket create(BucketInfo bucketInfo, BucketTargetOption... options) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public CopyWriter copy(CopyRequest copyRequest) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Blob compose(ComposeRequest composeRequest) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public StorageBatch batch() {
					// TODO Auto-generated method stub
					return null;
				}
			};
		}
	}
}
