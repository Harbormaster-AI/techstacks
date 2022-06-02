#header()
package ${aib.getRootPackageName()}.#getDelegatePackageName();

import com.amazonaws.services.lambda.runtime.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.*;

#if ( ${aib.getParam('aws-lambda.use kinesis')} == "true" ) 
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.KinesisException;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamResponse;
#end ##if ( ${aib.getParam('aws-lambda.use kinesis')} == "true" )

import ${aib.getRootPackageName(true)}.#getPrimaryKeyPackageName().BasePrimaryKey;


/**
 * Base class for AWS Lambda business delegates.
 * <p>
 * @author ${aib.getAuthor()}
 */
public class BaseAWSLambdaDelegate
{
    protected BaseAWSLambdaDelegate()
    {
    }

    protected static String getContextDetails( Context context ) {
    	StringBuilder details = new StringBuilder();
    	
    	if ( context != null )
    	{
	    	details.append("Function name: " + context.getFunctionName());
	        details.append("\nMax mem allocated: " + context.getMemoryLimitInMB());
	        details.append("\nTime remaining in milliseconds: " + context.getRemainingTimeInMillis());
	        details.append("\nCloudWatch log stream name: " + context.getLogStreamName());
	        details.append("\nCloudWatch log group name: " + context.getLogGroupName()); 
	        details.append("\n");
    	}
    	return( details.toString() );
    }
    
    protected static String toJson(Object obj) {
        return getGoogleJson().toJson(obj);
    }

    protected static Object fromJson(String json, Class objectClass) {
        return getGoogleJson().fromJson(json, objectClass);
    }

    protected static Gson getGoogleJson() {
        return GoogleJson;
    }

    protected void putRecordToKinesis( RecordData record) {
#if ( ${aib.getParam('aws-lambda.use kinesis')} == "true" ) 
    	String streamName 	= System.getEnv("kinesisStreamName");	// provider to the function as an env var
        Region region 		= ${aib.getParam("aws.region")};
        
        KinesisClient kinesisClient = KinesisClient.builder()
                .region(region)
                .build();

        byte[] bytes = record.toString().getBytes();

        PutRecordRequest request = PutRecordRequest.builder()
                .partitionKey(record.target) // We use the ticker symbol as the partition key, explained in the Supplemental Information section below.
                .streamName(streamName)
                .data(SdkBytes.fromByteArray(bytes))
                .build();
        
        try {
            // put the data as a record
        	kinesisClient.putRecord(request);
        } catch (KinesisException e) {
            e.getMessage();
        } finally {
            // close the kinesis client
            kinesisClient.close();        	
        }
#end##if ( ${aib.getParam('aws-lambda.use kinesis')} == "true" ) 
    	
    }
    
    static String call(String packageName, String actionName, Object arg)
        throws IOException {
    	String urlStr = DELEGATE_DAO_URL 
    						+ "/" + packageName + "/" + actionName;
    	
    	if ( arg != null )
    		urlStr = urlStr + "?" + toJson( arg ); 
    		
        URL url = new URL( urlStr );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        }

        BufferedReader rd = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();

        return sb.toString();
    }
    

    
// attributes
    private static Context context 					= null;
    final private static Gson GoogleJson 			= new Gson();
    protected final static String DELEGATE_DAO_URL 	= java.lang.System.getenv("delegateDAOHost") + ":" + java.lang.System.getenv("delegateDAOPort");

// inner class
    public class RecordData {
    	public RecordData( BasePrimaryKey parentKey, String action, String target, String msg ) {
    		this.parentKey = parentKey.keys().toString();
    		this.action = action;
    		this.target = target;
    		this.msg= msg;
    	}
    	
    	public String toString() {
    		StringBuilder builder = new StringBuilder();
    		builder.append("parentKey:");
    		builder.append(parentKey);
    		builder.append(", action:");
    		builder.append(action);
    		builder.append(", target:");
    		builder.append(target);
    		builder.append(", msg:");
    		builder.append(msg);

    	}
    	
    	// attributres 
    	public String parentKey;
    	public String action;
    	public String target;
    	public String msg;
    }
}

