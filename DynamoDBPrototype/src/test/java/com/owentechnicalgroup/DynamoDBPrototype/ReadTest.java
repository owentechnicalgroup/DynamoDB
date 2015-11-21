package com.owentechnicalgroup.DynamoDBPrototype;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class ReadTest extends CamelTestSupport {
	 
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;
 
    @Produce(uri = "direct:start")
    protected ProducerTemplate template;
    
    protected JndiRegistry createRegistry() throws Exception {
    	Region region = Region.getRegion(Regions.US_EAST_1);
    	JndiRegistry registry = new JndiRegistry();
    	
      	AWSCredentials credentials = new BasicAWSCredentials("AKIAJTVB6IW3O7CG6CHA","5aPoEXqwKSRy/Uo8c2Ux8C4dvrIgYWgO2e0wwxPq");
    	
    	AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentials);
	   	ddbClient.setRegion(region);

       registry.bind("AWSDDBClient", ddbClient);
       
     
    	
        return new JndiRegistry(createJndiContext());
    }
    
    
 
    @Test
    public void testSendMatchingMessage() throws Exception {
        String expectedBody = "<matched/>";
 
        resultEndpoint.expectedBodiesReceived(expectedBody);
 
        template.sendBodyAndHeader(expectedBody, "foo", "bar");
 
        resultEndpoint.assertIsSatisfied();
    }
 
    @Test
    public void testSendNotMatchingMessage() throws Exception {
        resultEndpoint.expectedMessageCount(0);
 
        template.sendBodyAndHeader("<notMatched/>", "foo", "notMatchedHeaderValue");
 
        resultEndpoint.assertIsSatisfied();
    }
 
    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                .from("aws-ddb://domainName?amazonDDBClient=AWSDDBClient&tableName=Accounts&operation=Query")
                .to("mock:result");
            }
        };
    }
}