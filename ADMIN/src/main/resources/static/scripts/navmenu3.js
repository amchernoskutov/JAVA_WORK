function addSystemNameButtion() {
  event.preventDefault();
  ajaxAddSystemNameForm();
};

function updateSystemNameButtion(id) {
  event.preventDefault();
  ajaxUpdateSystemNameForm(id);
};  

function deleteSystemNameButtion(id) {
  event.preventDefault();
  ajaxDeleteSystemNameForm(id);
};

function deleteGetSystemNameButtion(id) {
  event.preventDefault();
  ajaxDeleteGetSystemNameForm(id);
};

function updateGetSystemNameButtion(id) {
  event.preventDefault();
  ajaxUpdateGetSystemNameForm(id);
};  

function ajaxAddSystemNameForm() {
  var form = $('#sendSystemNameFormPost')[0];
  var data = new FormData(form);
  
  $.ajax({
    type: "post", 
    url: "./asuteradmin/systemname/add",
    data: data,
    processData: false,
    contentType: false,
    cache: false,
    timeout: 1000000,        
    success: function(result) {
      $('#sendSystemNameFormPostF').html(result);
      if (result.indexOf('<div class="font-weight-normal text-danger">') == -1) {
        $(".modal-backdrop.show").hide();
        $('#addSystemNameModal').modal('dispose');
      } else { 
        $(".modal-backdrop.show").hide();
        $('#addSystemNameModal').modal('show');
      }
    }
  });
};

function ajaxUpdateSystemNameForm(id) {
  var form = $('#sendSystemNameFormPost')[0];
  var data = new FormData(form);
  
  $.ajax({
    type: "post", 
    url: "./asuteradmin/systemname/edit/" + id,
    data: data,
    processData: false,
    contentType: false,
    cache: false,
    timeout: 1000000,        
    success: function(result) {
        $('#sendSystemNameFormPostF').html(result);
        if (result.indexOf('<div class="font-weight-normal text-danger">') == -1) {
          $(".modal-backdrop.show").hide();
          $('#updateSystemNameModal').modal('dispose');
        } else { 
          $(".modal-backdrop.show").hide();
          $('#updateSystemNameModal').modal('show');
        }
    }
  });
};

function ajaxUpdateGetSystemNameForm(id) {
  var form = $('#sendSystemNameFormPost')[0];
  var data = new FormData(form);
	  
  $.ajax({
    type: "get", 
    url: "./asuteradmin/systemname/edit/" + id,
    data: data,
    processData: false,
    contentType: false,
    cache: false,
    timeout: 1000000,        
    success: function(result) {
      $('#sendSystemNameFormPostF').html(result);
      $('#updateSystemNameModal').modal('show');
    }
  });
};

function ajaxDeleteSystemNameForm(id) {
  var form = $('#sendSystemNameFormPost')[0];
  var data = new FormData(form);

  $.ajax({
    type: "post", 
    url: "./asuteradmin/systemname/delete/" + id,
    data: data,
    processData: false,
    contentType: false,
    cache: false,
    timeout: 1000000,        
    success: function(result) {
      $('#sendSystemNameFormPostF').html(result);
      if (result.indexOf('<div class="font-weight-normal text-danger">') == -1) {
          $(".modal-backdrop.show").hide();
          $('#deleteSystemNameModal').modal('dispose');
        } else { 
          $(".modal-backdrop.show").hide();
          $('#deleteSystemNameModal').modal('show');
        }
    }
  });
};

function ajaxDeleteGetSystemNameForm(id) {
  var form = $('#sendSystemNameFormPost')[0];
  var data = new FormData(form);
		  
  $.ajax({
    type: "get", 
    url: "./asuteradmin/systemname/delete/" + id,
    data: data,
    processData: false,
    contentType: false,
    cache: false,
    timeout: 1000000,        
    success: function(result) {
      $('#sendSystemNameFormPostF').html(result);
      $('#deleteSystemNameModal').modal('show');
    }
  });
};
