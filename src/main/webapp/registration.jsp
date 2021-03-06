<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");%>
<html>
<head>
<meta charset="UTF-8">
<title>Registration</title>
<link rel="stylesheet" href="css/default.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<link rel="stylesheet" href="css/bootstrapValidator.min.css">
</head>
<body>
	<fieldset class="container-main">
		<legend>Registration</legend>
		<form id="formRegistration" class="form-horizontal"
			data-bv-message="This value is not valid"
			data-bv-feedbackicons-valid="glyphicon glyphicon-ok"
			data-bv-feedbackicons-invalid="glyphicon glyphicon-remove"
			data-bv-feedbackicons-validating="glyphicon glyphicon-refresh">
			<div class="form-group">
				<label for="fullName" class="col-sm-4 control-label">Full Name</label>
				<div class="col-sm-4">
					<input type="text" class="form-control" id="fullName" name="fullName"
						placeholder="Full Name" required="required" autofocus="autofocus">
				</div>
			</div>
			<div class="form-group">
				<label for="billingAddress" class="col-sm-4 control-label">Billing Address</label>
				<div class="col-sm-4">
					<input type="text" class="form-control" id="billingAddress" name="billingAddress"
						placeholder="Billing Address" required="required">
				</div>
			</div>			
			<div class="form-group">
				<label for="email" class="col-sm-4 control-label">Email</label>
				<div class="col-sm-4">
					<div class="input-group">
						<div class="input-group-addon">@</div>
						<input type="email" class="form-control" id="email" name="email"
							placeholder="mail@example.com" required="required">
					</div>
				</div>
			</div>
			<br/>
			<div class="form-group">
				<label for="login" class="col-sm-4 control-label">Login</label>
				<div class="col-sm-4">
					<input type="text" class="form-control" id="login" name="login"
						placeholder="Login" required="required" maxlength="30">
				</div>
			</div>			
			<div class="form-group">
				<label for="password" class="col-sm-4 control-label">Password</label>
				<div class="col-sm-4">
					<input type="password" class="form-control" id="password" name="password"
						placeholder="Password" required="required">
				</div>
			</div>
			<div class="form-group">
				<label for="confirmPassword" class="col-sm-4 control-label">Confirm Password</label>
				<div class="col-sm-4">
					<input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
						placeholder="Password" required="required">
				</div>
			</div>

			<div class="form-group">
				<div class="col-sm-offset-4 col-sm-4">
					<button type="submit" class="btn btn-primary btn-sm">Registration</button>
					<button type="reset" class="btn btn-info btn-sm" id="btnReset">Reset</button>
				</div>
			</div>
		</form>
	</fieldset>
	
<script type="text/javascript" data-main="js/formValidator" src="js/require.js"></script>
</body>
</html>