app.controller('product', function($scope, $http, $window, $location, facebookServices) {

    var dropzone;
    $scope.showError = false;
    $scope.disable= true;

    angular.element(document).ready(function () {
        $scope.id = $location.search().id
        if (angular.isUndefined($scope.id)) {
            dropzone = new Dropzone("#dropzoneProduct",{
              paramName: 'image',
              url:'/product/create',
              method: 'POST',
              maxFilesize: 5,
              maxFiles: 1,
              autoProcessQueue: false,
              acceptedFiles: '.jpg, .png, .jpeg',
              removedfile: function(file) {
                setDisable(true)
                var _ref;
                return (_ref = file.previewElement) != null ? _ref.parentNode.removeChild(file.previewElement) : void 0;
              },
              accept: function (file, done) {
                setDisable(false)
                done()
              },
              sending: function(file, xhr, formData) {
                formData.append("description", $scope.product.description)
                formData.append("name", $scope.product.name)
                formData.append("price", Math.round($scope.product.price*100)/100)
                formData.append("quantity", parseInt($scope.product.quantity))
                $window.location.href = '/';
              },
              addRemoveLinks: true,
              dictDefaultMessage: 'Please put picture (JPG, PNG, JPEG), less than 5 Mo'
            });
        }
        else {
            dropzone = new Dropzone("#dropzoneProduct",{
              paramName: 'image',
              url:'/product/create',
              method: 'POST',
              maxFilesize: 5,
              maxFiles: 1,
              autoProcessQueue: false,
              acceptedFiles: '.jpg, .png, .jpeg',
              removedfile: function(file) {
                setDisable(true)
                var _ref;
                return (_ref = file.previewElement) != null ? _ref.parentNode.removeChild(file.previewElement) : void 0;
              },
              accept: function (file, done) {
                setDisable(false)
                done()
              },
              sending: function(file, xhr, formData) {
                formData.append("description", $scope.product.description)
                formData.append("name", $scope.product.name)
                formData.append("price", Math.round($scope.product.price*100)/100)
                formData.append("quantity", parseInt($scope.product.quantity))
              },
              addRemoveLinks: true,
              dictDefaultMessage: 'Please put picture (JPG, PNG, JPEG), less than 5 Mo'
            });
        }
    })

    setDisable = function(b) {
        $scope.disable = b
        $scope.$apply()
    }

    /*//NE PAS EFFACER
    var xhr = new XMLHttpRequest();

    // Use JSFiddle logo as a sample image to avoid complicating
    // this example with cross-domain issues.
    xhr.open( "GET", "/retrieve", true );

    // Ask for the result as an ArrayBuffer.
    xhr.responseType = "arraybuffer";

    xhr.onload = function( e ) {
        console.log(this.response)
        // Obtain a blob: URL for the image data.
        var arrayBufferView = new Uint8Array( this.response );
        var blob = new Blob( [ arrayBufferView ], { type: "image/jpeg" } );
        var urlCreator = window.URL || window.webkitURL;
        var imageUrl = urlCreator.createObjectURL( blob );
        var img = document.querySelector( "#image" );
        img.src = imageUrl;
    };

    xhr.send();*/


    $scope.submitForm = function() {
        dropzone.processQueue()
    }
})