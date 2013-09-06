/**
 * Used by the web methods.
 * 
 * @author Renas Reda, renasreda@gmail.com
 * 
 */

function allWebElements() {
	for (var key in document.all){
		try{
			promptElement(document.all[key]);			
		}catch(ignored){}
	}
	finished();
}

function allTexts() {
	var range = document.createRange();
	var walk=document.createTreeWalker(document.body,NodeFilter.SHOW_TEXT,null,false); 
	while(n=walk.nextNode()){
		try{
			promptText(n, range);
		}catch(ignored){}
	} 
	finished();
}

function id(id) {
	var element = document.getElementById(id);
	if(element != null){ 
		promptElement(element);
	} 
	else {
		for (var key in document.all){
			try{
				element = document.all[key];
				if(element.id == id) {
					promptElement(element);
				}
			} catch(ignored){}			
		}
	}
	finished(); 
}

function xpath(xpath) {
	var elements = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null); 

	if (elements){
		var element = elements.iterateNext();
		while(element) {
			promptElement(element);
			element = result.iterateNext();
		}
		finished();
	}
}

function cssSelector(cssSelector) {
	var elements = document.querySelectorAll(cssSelector);
	for (var key in elements) {
		if(elements != null){ 
			try{
				promptElement(elements[key]);
			}catch(ignored){}  
		}
	}
	finished(); 
}

function name(name) {
	var walk=document.createTreeWalker(document.body,NodeFilter.SHOW_ELEMENT,null,false); 
	while(n=walk.nextNode()){
		try{
			var attributeName = n.getAttribute('name');
			if(attributeName != null && attributeName.trim().length>0 && attributeName == name){
				promptElement(n);
			}
		}catch(ignored){} 
	} 
	finished();
}

function className(nameOfClass) {
	var walk=document.createTreeWalker(document.body,NodeFilter.SHOW_ELEMENT,null,false); 
	while(n=walk.nextNode()){
		try{
			var className = n.className; 
			if(className != null && className.trim().length>0 && className == nameOfClass) {
				promptElement(n);
			}
		}catch(ignored){} 
	} 
	finished(); 
}

function textContent(text) {
	var range = document.createRange();
	var walk=document.createTreeWalker(document.body,NodeFilter.SHOW_TEXT,null,false); 
	while(n=walk.nextNode()){ 
		try{
			var textContent = n.textContent; 
			if(textContent.trim() == text.trim()){  
				promptText(n, range);
			}
		}catch(ignored){} 
	} 
	finished();  
}

function tagName(tagName) {
	var elements = document.getElementsByTagName(tagName);
	for (var key in elements) {
		if(elements != null){ 
			try{
				promptElement(elements[key]);
			}catch(ignored){}  
		}
	}
	finished();
}

function enterTextById(id, text) {
	var element = document.getElementById(id);
	if(element != null)
		element.value = text;

	finished(); 
}

function enterTextByXpath(xpath, text) {
	var element = document.evaluate(xpath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null ).singleNodeValue;
	if(element != null)
		element.value = text;

	finished(); 
}

function enterTextByCssSelector(cssSelector, text) {
	var element = document.querySelector(cssSelector);
	if(element != null)
		element.value = text;

	finished(); 
}

function enterTextByName(name, text) {
	var walk=document.createTreeWalker(document.body,NodeFilter.SHOW_ELEMENT,null,false); 
	while(n=walk.nextNode()){
		var attributeName = n.getAttribute('name');
		if(attributeName != null && attributeName.trim().length>0 && attributeName == name) 
			n.value=text;  
	} 
	finished();
}

function enterTextByClassName(name, text) {
	var walk=document.createTreeWalker(document.body,NodeFilter.SHOW_ELEMENT,null,false); 
	while(n=walk.nextNode()){
		var className = n.className; 
		if(className != null && className.trim().length>0 && className == name) 
			n.value=text;
	}
	finished();
}

function enterTextByTextContent(textContent, text) {
	var walk=document.createTreeWalker(document.body,NodeFilter.SHOW_TEXT,null,false); 
	while(n=walk.nextNode()){ 
		var textValue = n.textContent; 
		if(textValue == textContent) 
			n.parentNode.value = text; 
	}
	finished();
}

function enterTextByTagName(tagName, text) {
	var elements = document.getElementsByTagName(tagName);
	if(elements != null){
		elements[0].value = text;
	}
	finished();
}

function promptElement(element) {
	var id = element.id;
	var text = element.innerText;
	if(text.trim().length == 0){
		text = element.value;
	}
	var name = element.getAttribute('name');
	var className = element.className;
	var tagName = element.tagName;
	var attributes = "";
	var htmlAttributes = element.attributes;
	for (var i = 0, htmlAttribute; htmlAttribute = htmlAttributes[i]; i++){
		attributes += htmlAttribute.name + "::" + htmlAttribute.value;
		if (i + 1 < htmlAttributes.length) {
			attributes += "#$";
		}
	}
	
	var rect = element.getBoundingClientRect();
	if(rect.width > 0 && rect.height > 0 && rect.left >= 0 && rect.top >= 0){
		prompt(id + ';,' + text + ';,' + name + ";," + className + ";," + tagName + ";," + rect.left + ';,' + rect.top + ';,' + rect.width + ';,' + rect.height + ';,' + attributes);
	}
}

function promptText(element, range) {	
	var text = element.textContent;
	if(text.trim().length>0) {
		range.selectNodeContents(element);
		var rect = range.getBoundingClientRect();
		if(rect.width > 0 && rect.height > 0 && rect.left >= 0 && rect.top >= 0){
			var id = element.parentNode.id;
			var name = element.parentNode.getAttribute('name');
			var className = element.parentNode.className;
			var tagName = element.parentNode.tagName;
			prompt(id + ';,' + text + ';,' + name + ";," + className + ";," + tagName + ";," + rect.left + ';,' + rect.top + ';,' + rect.width + ';,' + rect.height);
		}
	}
}

function finished(){
	prompt('robotium-finished');
}
