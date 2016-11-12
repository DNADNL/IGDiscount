app.controller('product', function($scope, $http, $window, $location, facebookServices) {

    var dropzoneCreate;
    var dropzoneUpdate;
    $scope.showError = false;
    $scope.disable= true;
    $scope.product = {};
    $scope.creatingMode = true

    angular.element(document).ready(function () {
        $scope.id = $location.search().id
        dropzoneCreate = new Dropzone("#dropzoneProduct",{
          paramName: 'image',
          url:'/product',
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
          maxfilesexceeded: function(file) {
              this.removeAllFiles();
              this.addFile(file);
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

        if (!angular.isUndefined($scope.id)) {
            $scope.creatingMode = false

            dropzoneUpdate = new Dropzone("#dropzoneUpdate",{
              paramName: 'image',
              url:'/product/'+$scope.id+'/image',
              method: 'PUT',
              maxFilesize: 5,
              maxFiles: 1,
              autoProcessQueue: true,
              acceptedFiles: '.jpg, .png, .jpeg',
              removedfile: function(file) {
                setDisable(true)
                var _ref;
                return (_ref = file.previewElement) != null ? _ref.parentNode.removeChild(file.previewElement) : void 0;
              },
              maxfilesexceeded: function(file) {
                this.removeAllFiles();
                this.addFile(file);
              },
              accept: function (file, done) {
                setDisable(false)
                done()
              },
              sending: function(file, xhr, formData) {},
              addRemoveLinks: true,
              dictDefaultMessage: 'Please put picture (JPG, PNG, JPEG), less than 5 Mo'
            });

            var rqt = {
                method : 'GET',
                url : '/product/' + $scope.id,
                headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
            };

            $http(rqt).success(function(data){
                $scope.product.description = data.description
                $scope.product.name = data.name
                $scope.product.price = Math.round(data.price*100)/100
                $scope.product.quantity = data.quantity
                var arrayBuffer = data.image.content;
                var bytes = new Uint8Array(arrayBuffer);
                var blob = new Blob( [ bytes ], { type: data.image.mime } );
                var urlCreator = window.URL || window.webkitURL;
                var imageUrl = urlCreator.createObjectURL( blob );

                var mockFile = { name: data.name, accepted: true };
                dropzoneUpdate.emit("addedfile", mockFile);
                dropzoneUpdate.createThumbnailFromUrl(mockFile, imageUrl);
                dropzoneUpdate.emit("success", mockFile);
                dropzoneUpdate.emit("complete", mockFile);
                dropzoneUpdate.files.push(mockFile);
                $scope.disable = false
            })

        }
    })

    setDisable = function(b) {
        $scope.disable = b
        $scope.$apply()
    }

    $scope.submitForm = function() {
        if ($scope.creatingMode) {
            dropzoneCreate.processQueue()
        }
        else {
            var rqtUpdate = {
                method : 'PUT',
                data : $.param({
                         description : $scope.product.description,
                         name : $scope.product.name,
                         price : Math.round($scope.product.price*100)/100,
                         quantity : parseInt($scope.product.quantity)
                     }),
                url : '/product/' + $scope.id,
                headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
            };

            $http(rqtUpdate).success(function(data){
                $window.location.href = '/';
            })
        }
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
})