package com.jayway.android.robotium.solo;

import java.util.List;
import android.app.Instrumentation;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * WebChromeClient used to get information on web elements by injections of JavaScript. 
 * 
 * @author Renas Reda, renasreda@gmail.com
 * 
 */

class RobotiumWebClient extends WebChromeClient{
	WebElementCreator textViewCreator;
	private Instrumentation inst;
	private WebChromeClient robotiumWebClient;


	/**
	 * Constructs this object.
	 *
	 * @param instrumentation the {@code Instrumentation} instance
	 * @param textViewCreator the {@code TextViewCreator} instance
	 */

	public RobotiumWebClient(Instrumentation inst, WebElementCreator textViewCreator){
		this.inst = inst;
		this.textViewCreator = textViewCreator;
		robotiumWebClient = this;
	}

	/**
	 * Enables JavaScript in the given {@code WebViews} objects.
	 * 
	 * @param webViews the {@code WebView} objects to enable JavaScript in
	 */

	public void enableJavascriptAndSetRobotiumWebClient(List<WebView> webViews){
		for(final WebView webView : webViews){

			if(webView != null){ 

				inst.runOnMainSync(new Runnable() {
					@Override
					public void run() {
						webView.getSettings().setJavaScriptEnabled(true);
						webView.setWebChromeClient(robotiumWebClient);

					}
				});
			}
		}
	}

	/**
	 * Overrides onJsPrompt in order to create {@code TextView} objects based on the web elements information prompted by the injections of JavaScript
	 */

	@Override
	public boolean onJsPrompt(WebView view, String url, String message,	String defaultValue, JsPromptResult r) {

		if(message != null){

			if(message.equals("robotium-finished")){
				textViewCreator.setFinished(true);
			}
			else{
				textViewCreator.createWebElementAndAddInList(message, view);
			}
		}
		r.confirm();
		return true;
	}

}

