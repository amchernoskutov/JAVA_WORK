function loaderForm(progressBarValue) {
  event.preventDefault();
  ajaxLoaderForm(progressBarValue);
};  

function sendForm(progressBarSize) {
  event.preventDefault();
  ajaxSendForm(progressBarSize);
};  

function ajaxLoaderForm() {
  var form = $('#loaderFormPost')[0];
  var data = new FormData(form); 

  $.ajax({
    type: "post", 
    url: "./asuteradmin/loaderfile",
    data: data,
    processData: false,
    contentType: false,
    cache: false,
    timeout: 1000000,        
    success: function(result) {
      $('#loaderFormPostF').html(result);
    }
  });
};

function ajaxSendForm(progressBarSize) {
  var form = $('#sendFormPost')[0];
  var data = new FormData(form);
  var step = 0;

  if ((progressBarSize != null) & (progressBarSize > 0)) {
    $.ajax({
      type: "post", 
      url: "./asuteradmin/sendfile",
      data: data,
      processData: false,
      contentType: false,
      cache: false,
      timeout: 1000000,         
      success: function(result) {
        if (step < progressBarSize) {
          $('#loaderFormPostF').html(result);
          step++;
          ajaxSendForm(progressBarSize);
        } else {
          $('#loaderFormPostF').html(result);
        }
      }
    });
  }
};
