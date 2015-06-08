<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");%>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Item</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/default.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrapValidator.min.css">
</head>
<body>
	<%@include file="/WEB-INF/jspf/header.jspf" %>
	<fieldset class="container">
		<legend>Edit Item</legend>
		<form id="formEditItem" class="form-horizontal" action="<%=request.getContextPath()%>/items/save"
			data-bv-message="This value is not valid"
			data-bv-feedbackicons-valid="glyphicon glyphicon-ok"
			data-bv-feedbackicons-invalid="glyphicon glyphicon-remove"
			data-bv-feedbackicons-validating="glyphicon glyphicon-refresh">
			<input type="hidden" id="itemId" name="itemId" value="${ model.getId() }" ${ (model == null) ? "disabled='disabled'" : "" }>
			<div class="form-group">
				<label for="title" class="col-sm-4 control-label">Title of Item</label>
				<div class="col-sm-4">
					<input type="text" class="form-control" id="title" name="title"
						placeholder="Title of Item" required="required" autofocus="autofocus" value="${ model.getTitle() }">
				</div>
			</div>
			<div class="form-group">
				<label for="description" class="col-sm-4 control-label">Description</label>
				<div class="col-sm-4">
					<textarea rows="3" cols="" class="form-control" id="description" name="description"
						placeholder="Description" required="required">${ model.getDescription() }</textarea>
				</div>
			</div>			
			<div class="form-group">
				<label for="startPrice" class="col-sm-4 control-label">Start price</label>
				<div class="col-xs-2">
					<div class="input-group">
						<div class="input-group-addon">$</div>
						<input type="text" class="form-control" id="startPrice" name="startPrice"
							placeholder="0.00" required="required" min="0" value="${ model.getStartPrice() }">
					</div>
				</div>
			</div>
			<div class="form-group">
				<label for="timeLeft" class="col-sm-4 control-label">Time Left</label>
				<div class="col-xs-2">
					<input type="number" class="form-control" id="timeLeft" name="timeLeft"
						min="1" max="1000" step="1" placeholder="Time in hour" required="required" value="${ model.getTimeLeft() }">
				</div>
			</div>
			<div class="form-group">
				<label for="buyItNow" class="col-sm-4 control-label">Buy It Now</label>
				<div class="col-sm-4">
					<div class="checkbox">
						<label>
							<input type="checkbox" id="buyItNow" name="buyItNow" value="Y" aria-label="..." ${ model.isBuyItNow() ? "checked='checked'" : "" }>
						</label>
					</div>
				</div>
			</div>
			<div class="form-group" style="${ model.isBuyItNow() ? 'display: none' : '' }">
				<label for="bidIncrement" class="col-sm-4 control-label">Bid Increment</label>
				<div class="col-xs-2">
					<div class="input-group">
						<div class="input-group-addon">$</div>
						<input type="text" class="form-control" id="bidIncrement" name="bidIncrement"
							placeholder="0.00" required="required" min="0" value="${ model.getBidIncrement() }" ${ model.isBuyItNow() ? "disabled='disabled'" : "" }>
					</div>
				</div>
			</div>
			

			<div class="form-group">
				<div class="col-sm-offset-4 col-sm-4">
					<button type="submit" class="btn btn-primary btn-sm">Save</button>
					<button type="reset" class="btn btn-info btn-sm" id="btnReset">Reset</button>
				</div>
			</div>
		</form>
	</fieldset>
<script type="text/javascript" data-main="<%=request.getContextPath()%>/js/formValidator" src="<%=request.getContextPath()%>/js/require.js"></script>
</body>
</html>