package com.jayway.android.robotium.solo;

/**
 * Used in conjunction with the web methods. Examples are By.id(String id) and By.cssSelector(String selector).
 * 
 * @author Renas Reda, renasreda@gmail.com
 * 
 */

public abstract class By {

	/**
	 * Select a WebElement by its id.
	 * 
	 * @param id the id of the web element	
	 * @return the Id object
	 */

	public static By id(final String id) {
		return new Id(id); 

	}

	/**
	 * Select a WebElement by its xpath.
	 * 
	 * @param xpath the xpath of the web element
	 * @return the Xpath object
	 */

	public static By xpath(final String xpath) {
		return new Xpath(xpath); 

	}

	/**
	 * Select a WebElement by its css selector.
	 * 
	 * @param selectors the css selector of the web element
	 * @return the CssSelector object
	 */

	public static By cssSelector(final String selectors) {
		return new CssSelector(selectors); 

	}

	/**
	 * Select a WebElement by its name.
	 * 
	 * @param name the name of the web element
	 * @return the Name object
	 */

	public static By name(final String name) {
		return new Name(name); 

	}

	/**
	 * Select a WebElement by its class name.
	 * 
	 * @param className the class name of the web element
	 * @return the ClassName object
	 */

	public static By className(final String className) {
		return new ClassName(className); 

	}

	/**
	 * Select a WebElement by its text content.
	 * 
	 * @param textContent the text content of the web element
	 * @return the TextContent object
	 */

	public static By textContent(final String textContent) {
		return new Text(textContent); 

	}
	
	/**
	 * Select a WebElement by its tag name.
	 * 
	 * @param tagName the tag name of the web element
	 * @return the TagName object
	 */

	public static By tagName(final String tagName) {
		return new TagName(tagName); 

	}

	/**
	 * Returns the value. 
	 * 
	 * @return the value
	 */
	
	public String getValue(){
		return "";
	}


	static class Id extends By {
		private final String id;

		public Id(String id) {
			this.id = id;
		}

		@Override
		public String getValue(){
			return id;
		}
	}

	static class Xpath extends By {
		private final String xpath;

		public Xpath(String xpath) {
			this.xpath = xpath;
		}

		@Override
		public String getValue(){
			return xpath;
		}
	}

	static class CssSelector extends By {
		private final String selector;

		public CssSelector(String selector) {
			this.selector = selector;
		}

		@Override
		public String getValue(){
			return selector;
		}
	}

	static class Name extends By {
		private final String name;

		public Name(String name) {
			this.name = name;
		}

		@Override
		public String getValue(){
			return name;
		}
	}
	
	static class ClassName extends By {
		private final String className;

		public ClassName(String className) {
			this.className = className;
		}

		@Override
		public String getValue(){
			return className;
		}
	}
	
	static class Text extends By {
		private final String textContent;

		public Text(String textContent) {
			this.textContent = textContent;
		}

		@Override
		public String getValue(){
			return textContent;
		}
	}
	
	static class TagName extends By {
		private final String tagName;
		
		public TagName(String tagName){
			this.tagName = tagName;
		}
		
		@Override
		public String getValue(){
			return tagName;
		}
	}
}
