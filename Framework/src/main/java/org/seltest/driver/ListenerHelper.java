package org.seltest.driver;

import java.io.File;
import java.lang.annotation.Annotation;

import org.openqa.selenium.WebDriver;
import org.seltest.core.TestCase;
import org.seltest.core.TestInfo;
import org.seltest.test.LoggerUtil;
import org.seltest.test.ReportUtil;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;

import atu.testng.reports.ATUReports;

/**
 * Class contains all the implementation in driver listener . So as to avoid rebuilding jar 
 * after every change in DriverListener class
 * @author adityas
 *
 */
public class ListenerHelper {

	private LoggerUtil logger = LoggerUtil.getLogger();
	private static String parallelMode;
	private static Boolean suiteCalled = false; //TODO To avoid calling suite listeners twice

	// restricting to Package Access
	ListenerHelper(){

	}

	public void onTestStart(ITestResult result) {
		TestCase.setTestName(result.getName());
		logger.test("	(START)	-> Test Case :  ");
		processAnnotation(result);

	}

	public void onTestSuccess(ITestResult result) {
		logger.test("	(SUCCESS)	-> Test Case : ");
		setTestInfo();
		ReportUtil.reportResult("SUCCESS", result.getName(), "");
	}

	public void onTestFailure(ITestResult result) {
		logger.test("	(FAIL)	-> Test Case : ");
		setTestInfo();
		ReportUtil.reportResult("FAIL", result.getName(), "");

	}

	public void onTestSkipped(ITestResult result) {
		logger.test("	(SKIPPED)	-> Test Case : ");
		setTestInfo();
		ReportUtil.reportResult("SKIP", result.getName(), "");

	}

	public void beforeConfiguration(ITestResult result) {
		logger.test("	(START)	-> Config Name : ");
		
	}

	public void onConfigurationFailure(ITestResult result){
		logger.test("	(FAIL)	-> Config Name : ");
	}

	public void onConfigurationSkip(ITestResult result){
		logger.test("	(SKIPPED)	-> Config Name : ");
	}

	public void onConfigurationSuccess(ITestResult result){
		logger.test("	(SUCCESS)	-> Config Name : ");
	}

	public void onStart(ITestContext context) {
		logger.test("	(START)	 -> Tests Name : ",context.getName()); 
		if(parallelMode.equals("tests")){
			createWebDriver();
		}
	}

	public void onFinish(ITestContext context) {
		logger.test("	(FINISHED)	 -> Tests Name : ",context.getName()); 
		if(parallelMode.equals("tests")){
			quitWebDriver();
		}
	}

	public void onStart(ISuite suite) {
		
		parallelMode=suite.getParallel().toLowerCase();// Get parallel mode

		if(!suiteCalled){
			logger.info("");
			logger.info("	******* STARTED "+suite.getName().toUpperCase()+" ******");
			logger.info("");
			String path = new File("./","src/main/resources/atu.properties").getAbsolutePath();
			System.setProperty("atu.reporter.config", path);
			if(!parallelMode.equals("tests")){ // Only Tests supported
				createWebDriver();
			}
			suiteCalled=true;
		}
	}

	public void onFinish(ISuite suite) {
		if(suiteCalled){
			logger.info("");
			logger.info("	****** FINISHED "+suite.getName().toUpperCase()+" ******");
			logger.info("");
			if(!parallelMode.equals("tests")){
				quitWebDriver();
			}
			suiteCalled=false;
		}
	}

	private synchronized void createWebDriver(){
		
		WebDriver driver = DriverFactory.getDriver();
		logger.debug("Driver Created : "+driver.hashCode());
		DriverManager.setWebDriver(driver);		

	}

	private synchronized void quitWebDriver(){
		WebDriver driver = DriverManager.getDriver();
		if (driver != null) {
			logger.debug(" Driver Going to Quit "+driver.hashCode());
			driver.quit();
		}

	}

	private void setTestInfo(){
		try{
		if(TestCase.getAuthor()!=null){
			ATUReports.setAuthorInfo(TestCase.getAuthor(), TestCase.getDate(), TestCase.getVersion());
		}}catch(Exception e){
			//TODO inform user
		}
	}

	private void processAnnotation(ITestResult result) {
		Class<?> testClass = result.getTestClass().getRealClass();//TODO Change ?

		if(testClass.isAnnotationPresent(TestInfo.class)){
			Annotation annotation =testClass.getAnnotation(TestInfo.class);
			TestInfo testInfo = (TestInfo) annotation;

			TestCase.setAuthor(testInfo.author());
			TestCase.setDate(testInfo.lastModified());
			TestCase.setVersion(testInfo.version());
		}
	}
}
