package com.robotium.solo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import com.robotium.solo.Solo.Config;
import android.app.Instrumentation;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;


/**
 * Contains web related methods. Examples are:
 * enterTextIntoWebElement(), getWebTexts(), getWebElements().
 * 
 * @author Renas Reda, renas.reda@robotium.com
 * 
 */

class WebUtils {

	private ViewFetcher viewFetcher;
	private Instrumentation inst;
	RobotiumWebClient robotiumWebCLient;
	WebElementCreator webElementCreator;
	WebChromeClient originalWebChromeClient = null;
	private Config config;


	/**
	 * Constructs this object.
	 * 
	 * @param config the {@code Config} instance
	 * @param instrumentation the {@code Instrumentation} instance
	 * @param viewFetcher the {@code ViewFetcher} 
	 * @param sleeper the {@code Sleeper} instance
	 */

	public WebUtils(Config config, Instrumentation instrumentation, ViewFetcher viewFetcher, Sleeper sleeper){
		this.config = config;
		this.inst = instrumentation;
		this.viewFetcher = viewFetcher;
		webElementCreator = new WebElementCreator(sleeper);
		robotiumWebCLient = new RobotiumWebClient(instrumentation, webElementCreator);
	}

	/**
	 * Returns {@code TextView} objects based on web elements shown in the present WebViews
	 * 
	 * @param onlyFromVisibleWebViews true if only from visible WebViews
	 * @return an {@code ArrayList} of {@code TextViews}s created from the present {@code WebView}s 
	 */

	public ArrayList<TextView> getTextViewsFromWebView(){
		boolean javaScriptWasExecuted = executeJavaScriptFunction("allTexts();");	

		return createAndReturnTextViewsFromWebElements(javaScriptWasExecuted);	
	}

	/**
	 * Creates and returns TextView objects based on WebElements
	 * 
	 * @return an ArrayList with TextViews
	 */

	private ArrayList <TextView> createAndReturnTextViewsFromWebElements(boolean javaScriptWasExecuted){
		ArrayList<TextView> webElementsAsTextViews = new ArrayList<TextView>();

		if(javaScriptWasExecuted){
			for(WebElement webElement : webElementCreator.getWebElementsFromWebViews()){
				if(isWebElementSufficientlyShown(webElement)){
					RobotiumTextView textView = new RobotiumTextView(inst.getContext(), webElement.getText(), webElement.getLocationX(), webElement.getLocationY());
					webElementsAsTextViews.add(textView);
				}
			}	
		}
		return webElementsAsTextViews;		
	}

	/**
	 * Returns an ArrayList of WebElements currently shown in the active WebView.
	 * 
	 * @param onlySufficientlyVisible true if only sufficiently visible {@link WebElement} objects should be returned
	 * @return an {@code ArrayList} of the {@link WebElement} objects shown in the active WebView
	 */

	public ArrayList<WebElement> getWebElements(boolean onlySufficientlyVisible){
		boolean javaScriptWasExecuted = executeJavaScriptFunction("allWebElements();");
		
		return getWebElements(javaScriptWasExecuted, onlySufficientlyVisible);
	}

	/**
	 * Returns an ArrayList of WebElements of the specified By object currently shown in the active WebView.
	 * 
	 * @param by the By object. Examples are By.id("id") and By.name("name")
	 * @param onlySufficientlyVisible true if only sufficiently visible {@link WebElement} objects should be returned
	 * @return an {@code ArrayList} of the {@link WebElement} objects currently shown in the active WebView 
	 */

	public ArrayList<WebElement> getWebElements(final By by, boolean onlySufficientlyVisbile){
		boolean javaScriptWasExecuted = executeJavaScript(by, false);
		
		if(config.useJavaScriptToClickWebElements){
			if(!javaScriptWasExecuted){
				return new ArrayList<WebElement>();
			}
			return webElementCreator.getWebElementsFromWebViews();
		}

		return getWebElements(javaScriptWasExecuted, onlySufficientlyVisbile);
	}

	/**
	 * Returns the sufficiently shown WebElements
	 * 
	 * @param javaScriptWasExecuted true if JavaScript was executed
	 * @param onlySufficientlyVisible true if only sufficiently visible {@link WebElement} objects should be returned
	 * @return the sufficiently shown WebElements
	 */

	private ArrayList<WebElement> getWebElements(boolean javaScriptWasExecuted, boolean onlySufficientlyVisbile){
		ArrayList<WebElement> webElements = new ArrayList<WebElement>();

		if(javaScriptWasExecuted){
			for(WebElement webElement : webElementCreator.getWebElementsFromWebViews()){
				if(!onlySufficientlyVisbile){
					webElements.add(webElement);
				}
				else if(isWebElementSufficientlyShown(webElement)){
					webElements.add(webElement);
				}
			}
		}
		return webElements;
	}

	/**
	 * Prepares for start of JavaScript execution
	 * 
	 * @return the JavaScript as a String
	 */

	private String prepareForStartOfJavascriptExecution(List<WebView> webViews) {
		webElementCreator.prepareForStart();

		WebChromeClient currentWebChromeClient = getCurrentWebChromeClient();

		if(currentWebChromeClient != null && !currentWebChromeClient.getClass().isAssignableFrom(RobotiumWebClient.class)){
			originalWebChromeClient = currentWebChromeClient;	
		}
		robotiumWebCLient.enableJavascriptAndSetRobotiumWebClient(webViews, originalWebChromeClient);
		return getJavaScriptAsString();
	}
	
	/**
	 * Returns the current WebChromeClient through reflection
	 * 
	 * @return the current WebChromeClient
	 * 
	 */

	private WebChromeClient getCurrentWebChromeClient(){
		WebChromeClient currentWebChromeClient = null;

		Object currentWebView = viewFetcher.getFreshestView(viewFetcher.getCurrentViews(WebView.class, true));

		if (android.os.Build.VERSION.SDK_INT >= 16) {
			try{
				currentWebView = new Reflect(currentWebView).field("mProvider").out(Object.class);
			}catch(IllegalArgumentException ignored) {}
		}

		try{
			if (android.os.Build.VERSION.SDK_INT >= 19) {
				Object mClientAdapter = new Reflect(currentWebView).field("mContentsClientAdapter").out(Object.class);
				currentWebChromeClient = new Reflect(mClientAdapter).field("mWebChromeClient").out(WebChromeClient.class);
			}
			else {
				Object mCallbackProxy = new Reflect(currentWebView).field("mCallbackProxy").out(Object.class);
				currentWebChromeClient = new Reflect(mCallbackProxy).field("mWebChromeClient").out(WebChromeClient.class);
			}
		}catch(Exception ignored){}

		return currentWebChromeClient;
	}

	/**
	 * Enters text into a web element using the given By method
	 * 
	 * @param by the By object e.g. By.id("id");
	 * @param text the text to enter
	 */

	public void enterTextIntoWebElement(final By by, final String text){
		if(by instanceof By.Id){
			executeJavaScriptFunction("enterTextById(\""+by.getValue()+"\", \""+text+"\");");
		}
		else if(by instanceof By.Xpath){
			executeJavaScriptFunction("enterTextByXpath(\""+by.getValue()+"\", \""+text+"\");");
		}
		else if(by instanceof By.CssSelector){
			executeJavaScriptFunction("enterTextByCssSelector(\""+by.getValue()+"\", \""+text+"\");");
		}
		else if(by instanceof By.Name){
			executeJavaScriptFunction("enterTextByName(\""+by.getValue()+"\", \""+text+"\");");
		}
		else if(by instanceof By.ClassName){
			executeJavaScriptFunction("enterTextByClassName(\""+by.getValue()+"\", \""+text+"\");");
		}
		else if(by instanceof By.Text){
			executeJavaScriptFunction("enterTextByTextContent(\""+by.getValue()+"\", \""+text+"\");");
		}
		else if(by instanceof By.TagName){
			executeJavaScriptFunction("enterTextByTagName(\""+by.getValue()+"\", \""+text+"\");");
		}
	}

	/**
	 * Executes JavaScript determined by the given By object
	 * 
	 * @param by the By object e.g. By.id("id");
	 * @param shouldClick true if click should be performed
	 * @return true if JavaScript function was executed
	 */

	public boolean executeJavaScript(final By by, boolean shouldClick){
		if(by instanceof By.Id){
			return executeJavaScriptFunction("id(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");");
		}
		else if(by instanceof By.Xpath){
			return executeJavaScriptFunction("xpath(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");");
		}
		else if(by instanceof By.CssSelector){
			return executeJavaScriptFunction("cssSelector(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");");
		}
		else if(by instanceof By.Name){
			return executeJavaScriptFunction("name(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");");
		}
		else if(by instanceof By.ClassName){
			return executeJavaScriptFunction("className(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");");
		}
		else if(by instanceof By.Text){
			return executeJavaScriptFunction("textContent(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");");
		}
		else if(by instanceof By.TagName){
			return executeJavaScriptFunction("tagName(\""+by.getValue()+"\", \"" + String.valueOf(shouldClick) + "\");");
		}
		return false;
	}

	/**
	 * Executes the given JavaScript function
	 * 
	 * @param function the function as a String
	 * @return true if JavaScript function was executed
	 */

	private boolean executeJavaScriptFunction(final String function) {
		List<WebView> webViews = viewFetcher.getCurrentViews(WebView.class, true);
		final WebView webView = viewFetcher.getFreshestView((ArrayList<WebView>) webViews);
		
		if(webView == null) {
			return false;
		}

		final String javaScript = setWebFrame(prepareForStartOfJavascriptExecution(webViews));
		
		inst.runOnMainSync(new Runnable() {
			public void run() {
				if(webView != null){
					webView.loadUrl("javascript:" + javaScript + function);
				}
			}
		});
		return true;
	}
	
	private String setWebFrame(String javascript){
		String frame = config.webFrame;
		
		if(frame.isEmpty() || frame.equals("document")){
			return javascript;
		}
		javascript = javascript.replaceAll(Pattern.quote("document, "), "document.getElementById(\""+frame+"\").contentDocument, ");
		javascript = javascript.replaceAll(Pattern.quote("document.body, "), "document.getElementById(\""+frame+"\").contentDocument, ");
		return javascript;
	}

	/**
	 * Returns true if the view is sufficiently shown
	 *
	 * @param view the view to check
	 * @return true if the view is sufficiently shown
	 */

	public final boolean isWebElementSufficientlyShown(WebElement webElement){
		final WebView webView = viewFetcher.getFreshestView(viewFetcher.getCurrentViews(WebView.class, true));
		final int[] xyWebView = new int[2];

		if(webView != null && webElement != null){
			webView.getLocationOnScreen(xyWebView);

			if(xyWebView[1] + webView.getHeight() > webElement.getLocationY())
				return true;
		}
		return false;
	}
	
	/**
	 * Splits a name by upper case.
	 * 
	 * @param name the name to split
	 * @return a String with the split name
	 * 
	 */

	public String splitNameByUpperCase(String name) {
		String [] texts = name.split("(?=\\p{Upper})");
		StringBuilder stringToReturn = new StringBuilder();

		for(String string : texts){

			if(stringToReturn.length() > 0) {
				stringToReturn.append(" " + string.toLowerCase());
			}
			else {
				stringToReturn.append(string.toLowerCase());
			}
		}
		return stringToReturn.toString();
	}

	/**
	 * Returns the JavaScript file RobotiumWeb.js as a String
	 *  
	 * @return the JavaScript file RobotiumWeb.js as a {@code String} 
	 */

	private String getJavaScriptAsString() {
		InputStream fis = getClass().getResourceAsStream("RobotiumWeb.js");
		StringBuffer javaScript = new StringBuffer();

		try {
			BufferedReader input =  new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while (( line = input.readLine()) != null){
				javaScript.append(line);
				javaScript.append("\n");
			}
			input.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return javaScript.toString();
	}
}