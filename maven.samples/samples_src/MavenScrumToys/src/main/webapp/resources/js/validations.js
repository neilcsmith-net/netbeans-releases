/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009, 2016 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 */

// ===================================================================
// Author: Matt Kruse <matt@mattkruse.com>
// WWW: http://www.mattkruse.com/
//
// NOTICE: You may use this code for any purpose, commercial or
// private, without any further permission from the author. You may
// remove this notice from your final code if you wish, however it is
// appreciated by the author if at least my web site address is kept.
//
// You may *NOT* re-distribute this code in any way except through its
// use. That means, you can include it in your product, or your web
// site, or any other form where the code is actually being used. You
// may not put the plain javascript up on your site for download or
// include it in your javascript libraries for download. 
// If you wish to share this code with others, please just point them
// to the URL instead.
// Please DO NOT link directly to my .js files from your site. Copy
// the files to your server and use them there. Thank you.
// ===================================================================

// HISTORY
// ------------------------------------------------------------------
// December 29, 2003: Added the option to specify a delimiter for
//    multiple valued input field via getInputValue(), etc.

//-------------------------------------------------------------------
// Trim functions
//   Returns string with whitespace trimmed
//-------------------------------------------------------------------
function LTrim(str){
	if (str==null){return null;}
	for(var i=0;str.charAt(i)==" ";i++);
	return str.substring(i,str.length);
	}
function RTrim(str){
	if (str==null){return null;}
	for(var i=str.length-1;str.charAt(i)==" ";i--);
	return str.substring(0,i+1);
	}
function Trim(str){return LTrim(RTrim(str));}
function LTrimAll(str) {
	if (str==null){return str;}
	for (var i=0; str.charAt(i)==" " || str.charAt(i)=="\n" || str.charAt(i)=="\t"; i++);
	return str.substring(i,str.length);
	}
function RTrimAll(str) {
	if (str==null){return str;}
	for (var i=str.length-1; str.charAt(i)==" " || str.charAt(i)=="\n" || str.charAt(i)=="\t"; i--);
	return str.substring(0,i+1);
	}
function TrimAll(str) {
	return LTrimAll(RTrimAll(str));
	}
//-------------------------------------------------------------------
// isNull(value)
//   Returns true if value is null
//-------------------------------------------------------------------
function isNull(val){return(val==null);}

//-------------------------------------------------------------------
// isBlank(value)
//   Returns true if value only contains spaces
//-------------------------------------------------------------------
function isBlank(val){
	if(val==null){return true;}
	for(var i=0;i<val.length;i++) {
		if ((val.charAt(i)!=' ')&&(val.charAt(i)!="\t")&&(val.charAt(i)!="\n")&&(val.charAt(i)!="\r")){return false;}
		}
	return true;
	}

//-------------------------------------------------------------------
// isInteger(value)
//   Returns true if value contains all digits
//-------------------------------------------------------------------
function isInteger(val){
	if (isBlank(val)){return false;}
	for(var i=0;i<val.length;i++){
		if(!isDigit(val.charAt(i))){return false;}
		}
	return true;
	}

//-------------------------------------------------------------------
// isNumeric(value)
//   Returns true if value contains a positive float value
//-------------------------------------------------------------------
function isNumeric(val){return(parseFloat(val,10)==(val*1));}

//-------------------------------------------------------------------
// isArray(obj)
// Returns true if the object is an array, else false
//-------------------------------------------------------------------
function isArray(obj){return(typeof(obj.length)=="undefined")?false:true;}

//-------------------------------------------------------------------
// isDigit(value)
//   Returns true if value is a 1-character digit
//-------------------------------------------------------------------
function isDigit(num) {
	if (num.length>1){return false;}
	var string="1234567890";
	if (string.indexOf(num)!=-1){return true;}
	return false;
	}

//-------------------------------------------------------------------
// setNullIfBlank(input_object)
//   Sets a form field to "" if it isBlank()
//-------------------------------------------------------------------
function setNullIfBlank(obj){if(isBlank(obj.value)){obj.value="";}}

//-------------------------------------------------------------------
// setFieldsToUpperCase(input_object)
//   Sets value of form field toUpperCase() for all fields passed
//-------------------------------------------------------------------
function setFieldsToUpperCase(){
	for(var i=0;i<arguments.length;i++) {
		arguments[i].value = arguments[i].value.toUpperCase();
		}
	}

//-------------------------------------------------------------------
// disallowBlank(input_object[,message[,true]])
//   Checks a form field for a blank value. Optionally alerts if 
//   blank and focuses
//-------------------------------------------------------------------
function disallowBlank(obj){
	var msg=(arguments.length>1)?arguments[1]:"";
	var dofocus=(arguments.length>2)?arguments[2]:false;
	if (isBlank(getInputValue(obj))){
		if(!isBlank(msg)){alert(msg);}
		if(dofocus){
			if (isArray(obj) && (typeof(obj.type)=="undefined")) {obj=obj[0];}
			if(obj.type=="text"||obj.type=="textarea"||obj.type=="password") { obj.select(); }
			obj.focus();
			}
		return true;
		}
	return false;
	}

//-------------------------------------------------------------------
// disallowModify(input_object[,message[,true]])
//   Checks a form field for a value different than defaultValue. 
//   Optionally alerts and focuses
//-------------------------------------------------------------------
function disallowModify(obj){
	var msg=(arguments.length>1)?arguments[1]:"";
	var dofocus=(arguments.length>2)?arguments[2]:false;
	if (getInputValue(obj)!=getInputDefaultValue(obj)){
		if(!isBlank(msg)){alert(msg);}
		if(dofocus){
			if (isArray(obj) && (typeof(obj.type)=="undefined")) {obj=obj[0];}
			if(obj.type=="text"||obj.type=="textarea"||obj.type=="password") { obj.select(); }
			obj.focus();
			}
		setInputValue(obj,getInputDefaultValue(obj));
		return true;
		}
	return false;
	}

//-------------------------------------------------------------------
// commifyArray(array[,delimiter])
//   Take an array of values and turn it into a comma-separated string
//   Pass an optional second argument to specify a delimiter other than
//   comma.
//-------------------------------------------------------------------
function commifyArray(obj,delimiter){
	if (typeof(delimiter)=="undefined" || delimiter==null) {
		delimiter = ",";
		}
	var s="";
	if(obj==null||obj.length<=0){return s;}
	for(var i=0;i<obj.length;i++){
		s=s+((s=="")?"":delimiter)+obj[i].toString();
		}
	return s;
	}

//-------------------------------------------------------------------
// getSingleInputValue(input_object,use_default,delimiter)
//   Utility function used by others
//-------------------------------------------------------------------
function getSingleInputValue(obj,use_default,delimiter) {
	switch(obj.type){
		case 'radio': case 'checkbox': return(((use_default)?obj.defaultChecked:obj.checked)?obj.value:null);
		case 'text': case 'hidden': case 'textarea': return(use_default)?obj.defaultValue:obj.value;
		case 'password': return((use_default)?null:obj.value);
		case 'select-one':
			if (obj.options==null) { return null; }
			if(use_default){
				var o=obj.options;
				for(var i=0;i<o.length;i++){if(o[i].defaultSelected){return o[i].value;}}
				return o[0].value;
				}
			if (obj.selectedIndex<0){return null;}
			return(obj.options.length>0)?obj.options[obj.selectedIndex].value:null;
		case 'select-multiple': 
			if (obj.options==null) { return null; }
			var values=new Array();
			for(var i=0;i<obj.options.length;i++) {
				if((use_default&&obj.options[i].defaultSelected)||(!use_default&&obj.options[i].selected)) {
					values[values.length]=obj.options[i].value;
					}
				}
			return (values.length==0)?null:commifyArray(values,delimiter);
		}
	alert("FATAL ERROR: Field type "+obj.type+" is not supported for this function");
	return null;
	}

//-------------------------------------------------------------------
// getSingleInputText(input_object,use_default,delimiter)
//   Utility function used by others
//-------------------------------------------------------------------
function getSingleInputText(obj,use_default,delimiter) {
	switch(obj.type){
		case 'radio': case 'checkbox': 	return "";
		case 'text': case 'hidden': case 'textarea': return(use_default)?obj.defaultValue:obj.value;
		case 'password': return((use_default)?null:obj.value);
		case 'select-one':
			if (obj.options==null) { return null; }
			if(use_default){
				var o=obj.options;
				for(var i=0;i<o.length;i++){if(o[i].defaultSelected){return o[i].text;}}
				return o[0].text;
				}
			if (obj.selectedIndex<0){return null;}
			return(obj.options.length>0)?obj.options[obj.selectedIndex].text:null;
		case 'select-multiple': 
			if (obj.options==null) { return null; }
			var values=new Array();
			for(var i=0;i<obj.options.length;i++) {
				if((use_default&&obj.options[i].defaultSelected)||(!use_default&&obj.options[i].selected)) {
					values[values.length]=obj.options[i].text;
					}
				}
			return (values.length==0)?null:commifyArray(values,delimiter);
		}
	alert("FATAL ERROR: Field type "+obj.type+" is not supported for this function");
	return null;
	}

//-------------------------------------------------------------------
// setSingleInputValue(input_object,value)
//   Utility function used by others
//-------------------------------------------------------------------
function setSingleInputValue(obj,value) {
	switch(obj.type){
		case 'radio': case 'checkbox': if(obj.value==value){obj.checked=true;return true;}else{obj.checked=false;return false;}
		case 'text': case 'hidden': case 'textarea': case 'password': obj.value=value;return true;
		case 'select-one': case 'select-multiple': 
			var o=obj.options;
			for(var i=0;i<o.length;i++){
				if(o[i].value==value){o[i].selected=true;}
				else{o[i].selected=false;}
				}
			return true;
		}
	alert("FATAL ERROR: Field type "+obj.type+" is not supported for this function");
	return false;
	}

//-------------------------------------------------------------------
// getInputValue(input_object[,delimiter])
//   Get the value of any form input field
//   Multiple-select fields are returned as comma-separated values, or
//   delmited by the optional second argument
//   (Doesn't support input types: button,file,reset,submit)
//-------------------------------------------------------------------
function getInputValue(obj,delimiter) {
	var use_default=(arguments.length>2)?arguments[2]:false;
	if (isArray(obj) && (typeof(obj.type)=="undefined")) {
		var values=new Array();
		for(var i=0;i<obj.length;i++){
			var v=getSingleInputValue(obj[i],use_default,delimiter);
			if(v!=null){values[values.length]=v;}
			}
		return commifyArray(values,delimiter);
		}
	return getSingleInputValue(obj,use_default,delimiter);
	}

//-------------------------------------------------------------------
// getInputText(input_object[,delimiter])
//   Get the displayed text of any form input field
//   Multiple-select fields are returned as comma-separated values, or
//   delmited by the optional second argument
//   (Doesn't support input types: button,file,reset,submit)
//-------------------------------------------------------------------
function getInputText(obj,delimiter) {
	var use_default=(arguments.length>2)?arguments[2]:false;
	if (isArray(obj) && (typeof(obj.type)=="undefined")) {
		var values=new Array();
		for(var i=0;i<obj.length;i++){
			var v=getSingleInputText(obj[i],use_default,delimiter);
			if(v!=null){values[values.length]=v;}
			}
		return commifyArray(values,delimiter);
		}
	return getSingleInputText(obj,use_default,delimiter);
	}

//-------------------------------------------------------------------
// getInputDefaultValue(input_object[,delimiter])
//   Get the default value of any form input field when it was created
//   Multiple-select fields are returned as comma-separated values, or
//   delmited by the optional second argument
//   (Doesn't support input types: button,file,password,reset,submit)
//-------------------------------------------------------------------
function getInputDefaultValue(obj,delimiter){return getInputValue(obj,delimiter,true);}

//-------------------------------------------------------------------
// isChanged(input_object)
//   Returns true if input object's value has changed since it was
//   created.
//-------------------------------------------------------------------
function isChanged(obj){return(getInputValue(obj)!=getInputDefaultValue(obj));}

//-------------------------------------------------------------------
// setInputValue(obj,value)
//   Set the value of any form field. In cases where no matching value
//   is available (select, radio, etc) then no option will be selected
//   (Doesn't support input types: button,file,password,reset,submit)
//-------------------------------------------------------------------
function setInputValue(obj,value) {
	var use_default=(arguments.length>1)?arguments[1]:false;
	if(isArray(obj)&&(typeof(obj.type)=="undefined")){
		for(var i=0;i<obj.length;i++){setSingleInputValue(obj[i],value);}
		}
	else{setSingleInputValue(obj,value);}
	}
	
//-------------------------------------------------------------------
// isFormModified(form_object,hidden_fields,ignore_fields)
//   Check to see if anything in a form has been changed. By default
//   it will check all visible form elements and ignore all hidden 
//   fields. 
//   You can pass a comma-separated list of field names to check in
//   addition to visible fields (for hiddens, etc).
//   You can also pass a comma-separated list of field names to be
//   ignored in the check.
//-------------------------------------------------------------------
function isFormModified(theform,hidden_fields,ignore_fields){
	if(hidden_fields==null){hidden_fields="";}
	if(ignore_fields==null){ignore_fields="";}
	var hiddenFields=new Object();
	var ignoreFields=new Object();
	var i,field;
	var hidden_fields_array=hidden_fields.split(',');
	for (i=0;i<hidden_fields_array.length;i++) {
		hiddenFields[Trim(hidden_fields_array[i])]=true;
		}
	var ignore_fields_array=ignore_fields.split(',');
	for (i=0;i<ignore_fields_array.length;i++) {
		ignoreFields[Trim(ignore_fields_array[i])]=true;
		}
	for (i=0;i<theform.elements.length;i++) {
		var changed=false;
		var name=theform.elements[i].name;
		if(!isBlank(name)){
			var type=theform.elements[i].type;
			if(!ignoreFields[name]){
				if(type=="hidden"&&hiddenFields[name]){changed=isChanged(theform[name]);}
				else if(type=="hidden"){changed=false;}
				else {changed=isChanged(theform[name]);}
				}
			}
		if(changed){return true;}
		}
		return false;
	}
