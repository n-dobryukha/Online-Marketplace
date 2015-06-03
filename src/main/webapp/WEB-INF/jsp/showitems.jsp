<%-- <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");%>
<html>
<head>
<meta charset="UTF-8">
<title>Show Items</title>
<link rel="stylesheet" href="../css/default.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<link rel="stylesheet" href="https://cdn.datatables.net/plug-ins/1.10.7/integration/bootstrap/3/dataTables.bootstrap.css">
</head>
<body>
	<%@include file="/WEB-INF/jspf/header.jspf" %>
	<div class="container-fluid">
		<fieldset>
			<legend class="h4">Items</legend>
<%-- 			<c:set var="Role" scope="session" value="GUEST"/>
			<c:if test="${ Role != GUEST }">
				<p class="btn-group-xs">
					<button type="button" class="btn btn-primary">Show All Items</button>
					<button type="button" class="btn btn-primary">Show My Items</button>
					<a href="edititem.html" type="button" class="btn btn-primary" role="button">Sell</a>
				</p>
			</c:if> --%>
			<table id="dataTable" class="table table-striped table-bordered" width="100%">
				<col class="colWidth5">
				<col class="colWidth10">
				<col class="colWidth20">
				<col class="colWidth10">
				<col class="colWidth8">
				<col class="colWidth8">
				<col class="colWidth8">
				<col class="colWidth10">
				<col class="colWidth10">
				<col class="colWidth10">
				<thead>
					<tr>
						<th>UID</th>
						<th>Title</th>
						<th>Description</th>
						<th>Seller</th>
						<th>Start price</th>
						<th>Bid inc</th>
						<th>Best offer</th>
						<th>Bidder</th>
						<th>Stop date</th>
						<th>Bid</th>
					</tr>
				</thead>
				<tfoot>
					<tr>
						<th>UID</th>
						<th>Title</th>
						<th>Description</th>
						<th>Seller</th>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
					</tr>
				</tfoot>
			</table>
		</fieldset>
	</div>
<script type="text/javascript" data-main="../js/dataTabler" src="../js/require.js"></script>
</body>
</html>