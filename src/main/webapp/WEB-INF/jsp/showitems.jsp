<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");%>
<html>
<head>
<meta charset="UTF-8">
<title>Show Items</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/default.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<link rel="stylesheet" href="https://cdn.datatables.net/plug-ins/1.10.7/integration/bootstrap/3/dataTables.bootstrap.css">
</head>
<body>
	<%@include file="/WEB-INF/jspf/header.jspf" %>
	<input type='hidden' id='type' value='${ model }'/>
	<div class="container-fluid">
		<fieldset>
			<legend class="h4">${ model } Items</legend>

			<c:if test='${ sessionScope.Role == "USER" }'>
				<p class="btn-group-xs">
					<a href="<%=request.getContextPath()%>/items/show/all" type="button" class="btn btn-primary" role="button">Show All Items</a>
					<a href="<%=request.getContextPath()%>/items/show/my" type="button" class="btn btn-primary" role="button">Show My Items</a>
					<a href="<%=request.getContextPath()%>/items/new" type="button" class="btn btn-primary" role="button">Sell</a>
				</p>
			</c:if>
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
	<div class="modal fade" id="bidListModal" data-keyboard="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">Bidding History</h4>
				</div>
				<div class="modal-body">
					<table id="biddingTable" class="table table-striped table-bordered" width="100%">
						<thead>
							<tr>
								<th>№</th>
								<th>Bidder</th>
								<th>Amount</th>
								<th>Date Time</th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript" data-main="<%=request.getContextPath()%>/js/dataTabler" src="<%=request.getContextPath()%>/js/require.js"></script>
</body>
</html>