<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");%>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Item</title>
<link rel="stylesheet" href="css/default.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<link rel="stylesheet" href="css/bootstrapValidator.min.css">
</head>
<body>
	<%@include file="/WEB-INF/jspf/header.jspf" %>
	<fieldset class="container">
		<legend>Edit Item</legend>
		<form id="formEditItem" class="form-horizontal"
			data-bv-message="This value is not valid"
			data-bv-feedbackicons-valid="glyphicon glyphicon-ok"
			data-bv-feedbackicons-invalid="glyphicon glyphicon-remove"
			data-bv-feedbackicons-validating="glyphicon glyphicon-refresh">
			<div class="form-group">
				<label for="title" class="col-sm-4 control-label">Title of Item</label>
				<div class="col-sm-4">
					<input type="text" class="form-control" id="title" name="title"
						placeholder="Title of Item" required="required" autofocus="autofocus">
				</div>
			</div>
			<div class="form-group">
				<label for="description" class="col-sm-4 control-label">Description</label>
				<div class="col-sm-4">
					<textarea rows="3" cols="" class="form-control" id="description" name="description"
						placeholder="Description" required="required"></textarea>
				</div>
			</div>			
			<div class="form-group">
				<label for="startPrice" class="col-sm-4 control-label">Start price</label>
				<div class="col-xs-2">
					<div class="input-group">
						<div class="input-group-addon">$</div>
						<input type="text" class="form-control" id="startPrice" name="startPrice"
							placeholder="0.00" required="required" min="0">
					</div>
				</div>
			</div>
			<div class="form-group">
				<label for="timeLeft" class="col-sm-4 control-label">Time Left</label>
				<div class="col-xs-2">
					<input type="number" class="form-control" id="timeLeft" name="timeLeft"
						min="1" max="1000" step="1" placeholder="Time in hour" required="required">
				</div>
			</div>
			<div class="form-group">
				<label for="buyItNow" class="col-sm-4 control-label">Buy It Now</label>
				<div class="col-sm-4">
					<div class="checkbox">
						<label>
							<input type="checkbox" id="buyItNow" name="buyItNow" aria-label="...">
						</label>
					</div>
				</div>
			</div>
			<div class="form-group">
				<label for="bidIncrement" class="col-sm-4 control-label">Bid Increment</label>
				<div class="col-xs-2">
					<div class="input-group">
						<div class="input-group-addon">$</div>
						<input type="text" class="form-control" id="bidIncrement" name="bidIncrement"
							placeholder="0.00" required="required" min="0">
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
<script type="text/javascript" data-main="js/formValidator" src="js/require.js"></script>
</body>
</html>