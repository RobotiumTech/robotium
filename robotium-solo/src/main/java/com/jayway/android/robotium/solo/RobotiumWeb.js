/**
 * Used by the web methods.
 * 
 * @author Renas Reda, renasreda@gmail.com
 * 
 */

function allWebElements() {
	for (var key in document.all){
		try{
			var rect = document.all[key].getBoundingClientRect();
			if(rect.width > 0 && rect.height > 0 && rect.left > 0){
				var id = document.all[key].id;
				var text = document.all[key].innerText;
				var name = document.all[key].getAttribute('name');
				var className = document.all[key].className;
				var tagName = document.all[key].tagName;
				prompt(id + ';,' + text + ';,' + name + ";," + className + ";," + tagName + ";," + rect.left + ';,' + rect.top + ';,' + rect.width + ';,' + rect.height);
			}
		}catch(ignored){}
	} 
	finished();
}

function allTexts() {
	var walk=document.createTreeWalker(document.body,NodeFilter.SHOW_TEXT,null,false); 
	var range = document.createRange();
	while(n=walk.nextNode()){
		try{
			var text = n.textContent;
			if(text.trim().length>0) {
				range.selectNodeContents(n);
				var rect = range.getBoundingClientRect();
				if(rect.width > 0 && rect.height > 0 && rect.left > 0){
					var id = n.parentNode.id;
					var name = n.parentNode.getAttribute('name');
					var className = n.parentNode.className;
					var tagName = n.parentNode.tagName;
					prompt(id + ';,' + text + ';,' + name + ";," + className + ";," + tagName + ";," + rect.left + ';,' + rect.top + ';,' + rect.width + ';,' + rect.height);
				}
			}
		}catch(ignored){}
	} 
	finished();
}

function id(id) {
	var element = document.getElementById(id);
	if(element != null){ 
		var text = element.textContent;
		var name = element.getAttribute('name');
		var className = element.className;
		var tagName = element.tagName;
		var rect = element.getBoundingClientRect(); 
		prompt(id + ';,' + text + ';,' + name + ";," + className + ";," + tagName + ";," + rect.left + ';,' + rect.top + ';,' + rect.width + ';,' + rect.height); 
	} 
	finished(); 
}

function xpath(xpath) {
	var element = document.evaluate(xpath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null ).singleNodeValue; 
	if(element != null){ 
		var id = element.id;
		var text = element.textContent;
		var name = element.getAttribute('name');
		var className = element.className;
		var tagName = element.tagName;
		var rect = element.getBoundingClientRect(); 
		prompt(id + ';,' + text + ';,' + name + ";," + className + ";," + tagName + ";," + rect.left + ';,' + rect.top + ';,' + rect.width + ';,' + rect.height); 
	} 
	finished();
}

function cssSelector(cssSelector) {
	var element = document.querySelector(cssSelector); 
	if(element != null){ 
		var id = element.id;
		var text = element.textContent;
		var name = element.getAttribute('name');
		var className = element.className;
		var tagName = element.tagName;
		var rect = element.getBoundingClientRect(); 
		prompt(id + ';,' + text + ';,' + name + ";," + className + ";," + tagName + ";," + rect.left + ';,' + rect.top + ';,' + rect.width + ';,' + rect.height); 
	} 
	finished(); 
}

function name(name) {
	var walk=document.createTreeWalker(document.body,NodeFilter.SHOW_ELEMENT,null,false); 
	while(n=walk.nextNode()){
		try{
			var attributeName = n.getAttribute('name');
			if(attributeName != null && attributeName.trim().length>0 && attributeName == name){
				var id = n.id;
				var text = n.textContent;
				var name = n.getAttribute('name');
				var className = n.className;
				var tagName = n.tagName;
				var rect = n.getBoundingClientRect();
				if(rect.width > 0 && rect.height > 0 && rect.left > 0){
					prompt(id + ';,' + text + ';,' + name + ";," + className + ";," + tagName + ";," + rect.left + ';,' + rect.top + ';,' + rect.width + ';,' + rect.height); 
				}
			}
		}catch(ignored){} 
	} 
	finished();
}

function className(name) {
	var walk=document.createTreeWalker(document.body,NodeFilter.SHOW_ELEMENT,null,false); 
	while(n=walk.nextNode()){
		try{
			var className = n.className; 
			if(className != null && className.trim().length>0 && className == name) {
				var id = n.id;
				var text = n.textContent;
				var name = n.getAttribute('name');
				var tagName = n.tagName;
				var rect = n.getBoundingClientRect();
				if(rect.width > 0 && rect.height > 0 && rect.left > 0){
					prompt(id + ';,' + text + ';,' + name + ";," + className + ";," + tagName + ";," + rect.left + ';,' + rect.top + ';,' + rect.width + ';,' + rect.height);
				}
			}
		}catch(ignored){} 
	} 
	finished(); 
}

function textContent(text) {
	var walk=document.createTreeWalker(document.body,NodeFilter.SHOW_TEXT,null,false); 
	var range = document.createRange();
	while(n=walk.nextNode()){ 
		try{
			var textContent = n.textContent; 
			if(textContent.trim() == text){  
				var id = n.parentNode.id;
				var name = n.parentNode.getAttribute('name');
				var className = n.parentNode.className;
				var tagName = n.parentNode.tagName;
				range.selectNodeContents(n);
				var rect = range.getBoundingClientRect();
				if(rect.width > 0 && rect.height > 0 && rect.left > 0){
					prompt(id + ';,' + textContent + ';,' + name + ";," + className + ";," + tagName + ";," + rect.left + ';,' + rect.top + ';,' + rect.width + ';,' + rect.height); 
				}
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
				var id = elements[key].id;
				var text = elements[key].textContent;
				var name = elements[key].getAttribute('name');
				var className = elements[key].className;
				var tagName = elements[key].tagName;
				var rect = elements[key].getBoundingClientRect(); 
				if(rect.width > 0 && rect.height > 0 && rect.left > 0){
					prompt(id + ';,' + text + ';,' + name + ";," + className + ";," + tagName + ";," + rect.left + ';,' + rect.top + ';,' + rect.width + ';,' + rect.height); 
				}
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

function finished(){
	prompt('robotium-finished');
}
