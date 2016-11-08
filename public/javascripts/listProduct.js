var app = angular.module('app', ['smart-table']);

app.controller('listProduct', function($scope, $filter, $http, $window) {

    $scope.productRows = []
    $scope.product = {}

    angular.element(document).ready(function() {

        var rqt = {
            method : 'GET',
            url : '/product',
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };

        $http(rqt).success(function(data){
            var xhr = new XMLHttpRequest();
            console.log(data)
            console.log(data)
            for (var i = 0; i < data.length; i++) {
                var arrayBuffer = data[i].image.content;
                var bytes = new Uint8Array(arrayBuffer);
                var blob = new Blob( [ bytes ], { type: data[i].image.mime } );
                var urlCreator = window.URL || window.webkitURL;
                var imageUrl = urlCreator.createObjectURL( blob );


                $scope.productRows.push({
                    id : data[i].id,
                    image: imageUrl,
                    seller: data[i].seller.companyName,
                    name : data[i].name,
                    price : data[i].price + "€",
                    quantity : data[i].quantity,
                    description : data[i].description
                });
            }
         })
    })

    $scope.show = function(id) {
        $scope.product.name = $scope.productRows[id].name
        $scope.product.image = $scope.productRows[id].image
        $scope.product.price = $scope.productRows[id].price
        $scope.product.quantity = $scope.productRows[id].quantity
        $scope.product.description = $scope.productRows[id].description
        $('#modal-product').modal();
        $('#modal-product').modal('show');
    }

     $scope.displayedCollection = [].concat($scope.productRows);


})