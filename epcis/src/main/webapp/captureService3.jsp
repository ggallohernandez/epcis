
<!DOCTYPE html>
<html>
<head>

<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description"
	content="Tutorial for EPCIS v1.1. It peaks three different EPCIS events in the life of Cow">
<meta name="author" content="Jaewook Jack Byun">

<title>Write Your Events</title>

<link rel="stylesheet" href="./css/bootstrap.min.css">

<script
	src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js?autoload=true&amp;skin=desert&amp;lang=html"
	defer="defer"></script>
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script>
	var baseURL = "http://localhost:8081/epcis";
</script>

<style>
.operative {
	font-weight: bold;
	border: 1px solid yellow
}
</style>
</head>

<body>

	<nav class="navbar navbar-default">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#navbar" aria-expanded="false"
					aria-controls="navbar">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">Oliot EPCIS Tutorial</a>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
				<form class="navbar-form navbar-right">
					<button type="button" class="btn btn-success"
						onclick="backToMainPage()">Back to Tutorial Page</button>
				</form>
			</div>
			<!--/.navbar-collapse -->
		</div>
	</nav>

	<div class="row">
		<div class="col-md-7">
			<div class="span4">
				<?prettify lang=html linenums=1?>
				<pre class="prettyprint" id="transaction"
					style="text-align: left; border: 4px solid #88c; width: 100%; height: 100%">
		</pre>
			</div>
		</div>
		<div class="col-md-4">
			<div class="list-group">
				<a href="#" class="list-group-item active"
					style="text-align: center"> Object Event </a> <a href="#"
					class="list-group-item">An ObjectEvent captures information
					about an event pertaining to one or more physical or digital
					objects</a> <a href="#" class="list-group-item active"
					style="text-align: center"> Object Event Example - In the
					retail shop </a> <a href="#" class="list-group-item"> &#8226; When:
					2015-01-03T20:33:31 <br> &#8226; What:
					urn:epc:id:sgtin:4012345.077889.27 (Beef part) <br>
					&nbsp;&nbsp;&nbsp; &#8226; 0.6kg <br> &#8226; Where:
					urn:epc:id:sgln:0614141.07346.1235 (Retail Shop) <br> &#8226;
					Why: The beef pack is on display for sale <br>
					&nbsp;&nbsp;&nbsp; &#8226; Business Step: retail_selling <br>
					&nbsp;&nbsp;&nbsp; &#8226; Disposition: sellable_accessible
				</a>
			</div>
			<button type="button" class="btn btn-danger" onclick="back()">Back to previous step</button>
			<button type="button" class="btn btn-warning" onclick="capture()">Capture
				this event</button>
			<button type="button" class="btn btn-info" onclick="skip()">Skip to next step</button>
			<br>
			<br>
			<footer>
			<p>&copy; Real time Embedded System Laboratory (RESL), Auto-ID
				Labs@Korea 2015</p>
			<p>
				Jaewook Jack Byun, Ph.D student<br>Korea Advanced Institute of
				Science and Technology (KAIST) <br>bjw0829@kaist.ac.kr,
				bjw0829@gmail.com
			</p>
			</footer>
		</div>
	</div>

	<!-- Parker Ranch 		  20.019786, -155.681829 -->
	<!--  Butcher Lauren B    19.634746, -155.986547  -->
	<!-- Matsuyama Food Mart  19.708886, -155.893430 -->

	<!-- Language hints can be put in XML application directive style comments. -->



	<script>
		function html(s) {
			return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g,
					'&gt;');
		}

		$("#transaction").load("./exampleXML/cow-object.xml",
				function(responseTxt, statusTxt, xhr) {
					x = html(responseTxt);
					document.getElementById("transaction").innerHTML = x;
				});

		function capture() {
			$("#transaction")
					.load(
							"./exampleXML/cow-object.xml",
							function(responseTxt, statusTxt, xhr) {

								$
										.ajax(
												{
													type : "POST",
													url : baseURL+"/Service/EventCapture",
													contentType : "application/xml; charset=utf-8",
													data : responseTxt
												})
										.done(
												function() {
													alert("Event is successfully stored");
													document.location.href = "./mapService.jsp";
												});
							});
		}
		
		function back(){
			document.location.href = "./captureService2.jsp";
		}
		
		function skip(){
			document.location.href = "./mapService.jsp";
		}
		function backToMainPage(){
			document.location.href = "./tutorialPage.jsp";
		}
	</script>
</body>
</html>