/**
 * 
 */
package org.seltest.test;

import org.openqa.selenium.WebDriver;
import org.seltest.core.StepUtil;
import org.testng.asserts.Assertion;
import org.testng.asserts.IAssert;


/**
 * @author adityas
 *
 */
public class HardAssertion extends Assertion {

	private final LoggerUtil logger = LoggerUtil.getLogger();
	private final int MAX_RETRY = 5;
	private final int RETRY_WAIT = 5;

	@Override
	public void onAssertFailure(IAssert assertCommand, AssertionError ex) {
		logger.assertion(" (ASSERT FAILED) ",assertCommand);
		throw new AssertionError(ex);
	}

	@Override
	public void onAssertSuccess(IAssert assertCommand) {
		logger.assertion("	(ASSERT SUCCESS) ", assertCommand);
	}

	public void assertTitle(WebDriver driver ,String expectedTitle){
		int retry =0;
		StepUtil.waitForPageLoaded(driver);
		String actual=null;
		while(retry<MAX_RETRY){
			actual =driver.getTitle();
			try{
				assert(actual.equals(expectedTitle));
				break;
			}catch(AssertionError e){
				StepUtil.simpleWait(RETRY_WAIT);
				logger.web("( HANDLED EXCEPTION) 	-> Message = "+e.getClass());
			}finally{
				retry++;
			}
		}
		assertEquals(actual, expectedTitle);

	}

	public void assertUrl(WebDriver driver , String expectedUrl){
		int retry =0;
		StepUtil.waitForPageLoaded(driver);
		String actual = null;
		while(retry<MAX_RETRY){
			actual =driver.getCurrentUrl().split("\\?")[0];// Remove parameters
			try{
				assert(actual.equals(expectedUrl));
				break;
			}catch(AssertionError e){
				StepUtil.simpleWait(RETRY_WAIT);
				logger.web("( HANDLED EXCEPTION) 	-> Message = "+e.getClass());
			}finally{
				retry++;
			}
		}
		assertEquals(actual, expectedUrl);
	}

	@Override
	public void onAfterAssert(IAssert assertCommand) {
		//	Not Implemented
	}

	@Override
	public void onBeforeAssert(IAssert assertCommand) {
		//	Not Implemented
	}

}
