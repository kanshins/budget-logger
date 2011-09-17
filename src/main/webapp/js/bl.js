
function setRecord(id,date,type,amount,category,desc) {
	document.getElementById("recordId_id").value = id;
	document.getElementById("date_id").value = date;
	document.getElementById("type_id").value = type;
	document.getElementById("amount_id").value = amount;
	document.getElementById("category_id").value = category;		
	document.getElementById("desc_id").value = desc;
	document.getElementById("desc_id").focus();
}

var count = 1;
function addRowDefault() {
	addRow("","");
}
function addRow(amount, desc) {
	var td = document.getElementById("rows_td_id");
	var div = document.createElement('div');
	if (null == desc || null == amount) {
		div.innerHTML = "<input id='input_sum_id_'" + count + "' onkeypress='return keyPressed(event)' type='text' name='sum" + count + "' class='sum'/>&nbsp;<input onkeypress='return keyPressed(event)' type='text' name='desc" + count + "' class='desc'/>";
	} else {
		div.innerHTML = "<input id='input_sum_id_'" + count + "' onkeypress='return keyPressed(event)' type='text' name='sum" + count + "' value='" + amount + "' class='sum'/>&nbsp;<input onkeypress='return keyPressed(event)' type='text' name='desc" + count + "' value='" + desc + "' class='desc'/>";
	}
	td.appendChild(div);
	document.getElementById("row_count_input_id").value = "" + count;
	//document.getElementById("input_sum_id_" + count).focus();
	count++;
}

function setCategory(id,name,def,report) {
	document.getElementById("categoryId_id").value = id;
	document.getElementById("category_def_id").checked = def;
	document.getElementById("categoryDef_id").value = def;
	document.getElementById("category_report_id").checked = report;
	document.getElementById("categoryReport_id").value = report;
	document.getElementById("category_name_id").value = name;
	document.getElementById("category_name_id").focus();
}

function keyPressed(e) {
    if (e.keyCode == 13) {
    	addRow("","");
    	return false;
    }
}
