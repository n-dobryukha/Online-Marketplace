/**
 * 
 */
require.config({
	paths: {
        'jquery': 'https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min',
        'datatables': 'https://cdn.datatables.net/1.10.7/js/jquery.dataTables',
        'integration': 'https://cdn.datatables.net/plug-ins/1.10.7/integration/bootstrap/3/dataTables.bootstrap',
        'bootstrap': 'https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min'        
    },
    shim: {
    	'datatables': ['jquery'],
    	'bootstrap': ['jquery'],
    	'bootstrapValidator.min': ['jquery']
    }
});

require(
		['jquery', 'datatables', 'integration', 'bootstrap', 'bootstrapValidator.min'],
		function( $, datatables, integration ) {
		    $(document).ready(function() {
		    	$('#dataTable tfoot th').each(
						function() {
							var title = $('#dataTable tfoot th').eq(
									$(this).index()).text();
							if (title !== "")
								$(this).html(
										'<input type="text" class="input-xs col-xs-12" placeholder="'
												+ title + '" />');
						});

				var table = $('#dataTable').DataTable({
					'dom': 'lt<"row"<"col-sm-5"i><"col-sm-7 input-group-sm"p>>',
					'searching' : true,
					'ajax' : {
						'url': './show/all',
						'type': 'POST'
					},
					'columnDefs': [
						{'targets':0, 'data' : 'uid'},
						{'targets':1, 'data' : 'title'},
						{'targets':2, 'data' : 'description'},
						{'targets':3, 'data' : 'seller.name' },
						{'targets':4, 'data' : 'startPrice'},
						{'targets':5, 'data' : 'bidInc'},
						{
							'targets':6,
							'data' : 'bestOffer',
							'render': function(data, type, row) {
								switch (data) {
								case "":
									return "";
									break;
								default:
									return data + "&nbsp<span class='glyphicon glyphicon-list-alt' aria-hidden='true' data-toggle='modal' data-target='#bidListModal' data-whatever='" + row.uid + "'></span>";
								}
								
							}
						},
						{'targets':7, 'data' : 'bidder.name'},
						{'targets':8, 'data' : { _: 'stopDate.display', 'sort': 'stopDate.timestamp' } },
						{
							'targets': 9,
							'data': 'action',
							'render': function(data, type, row) {
								switch (data) {
								case 'bid':
									var minValue = ((row.bestOffer === "") ? row.startPrice : (parseFloat(row.bestOffer) + parseFloat(row.bidInc)).toFixed(2));
									return "<form method='post' data-item-id='" + row.uid + "'><div class='form-group'><div class='input-group input-group-xs'><div class='input-group-addon'>$</div><input type='text' name='bidValue' class='form-control' placeholder='" + minValue + "' min='" + minValue + "' required='required'><span class='input-group-btn'><button class='btn btn-default' type='submit'>Bid</button></span></div></div></form>";
									break;
								case 'buy':
									return "<form method='post' data-item-id='" + row.uid + "'><input type='hidden' name='bidValue' value='" + row.startPrice + "'><div class='btn-group btn-group-justified'><div class='btn-group' role='group'><button class='btn btn-default btn-xs' type='submit'>Buy</button></div></div></form>"
								default:
									return "";
								}								
							}
						}]
				});

				table.columns().every(function() {
					var that = this;

					$('input', this.footer()).on('keyup change', function() {
						that.search(this.value).draw();
					});
				});
				
				table.on( 'draw', function () {
					$('#dataTable').find('form').each(function() {
						var $form = $(this);
						$form.bootstrapValidator({
							message: 'This value is not valid',						
						})
						.on('success.form.bv', function(e) {
			    			e.preventDefault();
				            var $form = $(e.target),
				            	itemId = $form.data('item-id'),
				            	value = $form.find('input[name="bidValue"]').val();
				            	bv = $form.data('bootstrapValidator'),
				            	data = $form.serialize();
				            
				            if (!confirm('Are you sure?')) {
				            	$form.find('button[type="submit"]').prop('disabled', false);
				            	return;
				            }
				            	
				            $.ajax({
			                    url: "./bid/" + itemId,
			                    type: "POST",
			                    data: data,
			                    cache: false,
			                    datatype: 'json',
			                         
			                    success: function (data, textStatus, jqXHR){
			                    	table.row( $form.parent().parent() ).data( JSON.parse(data.data) ).draw();
			                    },
			                         
			                    error: function (jqXHR, textStatus, errorThrown){
			                        alert("error - HTTP STATUS: "+jqXHR.status);
			                    },
			                         
			                    complete: function(jqXHR, textStatus){			                        
			                    }                    
			                });
			    		});
					})
				});
				
				$('#bidListModal').on('show.bs.modal', function (event) {					
					var itemId = $(event.relatedTarget).data('whatever');
					var table;
					if ( $.fn.dataTable.isDataTable( '#biddingTable' ) ) {
					    table = $('#biddingTable').DataTable();
					    table.ajax.url( './bids/' + itemId ).load();
					}
					else {
						table = $('#biddingTable').DataTable({
							'retrieve': true,
							'ajax' : {
								'url': './bids/' + itemId,
								'type': 'POST'
							},
							'columns': [
								{ data : 'count'},
								{ data : 'bidder.name'},
								{ data : 'amount'},
								{ data : { _: 'ts.display', 'sort': 'ts.timestamp' } },
							]
						});
					}
				})
				
			})
		}
);