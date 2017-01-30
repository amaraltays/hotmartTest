(function( $, plupload ) {


	// Instantiate the Plupload uploader.
	var uploader = new plupload.Uploader({

		// The upload URL - this is where chunks OR full files will go.
		url: "rest/FileUploadedService/uploadLargeFile",

		// To enable click-to-select-files, you can provide a browse button. We can
		// use the same one as the drop zone.
		browse_button: "selectFile",

		// Send any additional params (ie, multipart_params) in multipart message
		// format.
		multipart: true,

		// This defines the maximum size that each file chunk can be.
		// --
		// NOTE: I'm setting it particularly low for the demo. In general, you don't
		// want it to be too small because the chunking has a performance hit. Chunking
		// is meant for fault-tolerance and browser limitations.
		chunk_size: "1Mb",

		// If the upload of a chunk fails, this is the number of times the chunk
		// should be re-uploaded before the upload (overall) is considered a failure.
		max_retries: 3

	});


})( jQuery, plupload );
