var app = angular.module('app', ['smart-table', 'angularSpinner', 'ui-notification']);

app.controller('listProduct', function($scope, $filter, $http, $window, usSpinnerService, Notification) {

    $scope.productRows = []
    $scope.product = {}
    $scope.totalAmount = 0

    angular.element(document).ready(function() {

        var rqtKindOfUser = {
            method : 'GET',
            url : '/kindOfUser',
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };

        usSpinnerService.spin('spinner-1');
        $scope.startcounter++;

        $http(rqtKindOfUser).success(function(data){
            if (data.kindOfUser == "Simple User") {
                var rqtProduct = {
                    method : 'GET',
                    url : '/simpleUser/'+ data.id +'/product',
                    headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
                }

                $scope.idSimpleUser = data.id

                $http(rqtProduct).success(function(data){

                    var xhr = new XMLHttpRequest();
                    for (var i = 0; i < data.length; i++) {
                        var arrayBuffer = data[i].product.image.content;
                        var bytes = new Uint8Array(arrayBuffer);
                        var blob = new Blob( [ bytes ], { type: data[i].product.image.mime } );
                        var urlCreator = window.URL || window.webkitURL;
                        var imageUrl = urlCreator.createObjectURL( blob );

                        $scope.totalAmount += (Math.round(data[i].product.price*100)/100) * data[i].quantity
                        var lackStock = false

                        if (data[i].quantity > data[i].product.quantity) {
                            Notification.warning({message: 'Quantity of '+ data[i].product.name + ' is not available, please change quantity', delay: 20000});
                        }

                        $scope.productRows.push({
                            id : data[i].product.id,
                            image: imageUrl,
                            seller: data[i].product.seller.companyName,
                            name : data[i].product.name,
                            price : (Math.round(data[i].product.price*100)/100),
                            quantity : data[i].quantity,
                            quantityMax : data[i].product.quantity,
                            description : data[i].product.description
                        });
                    }
                    usSpinnerService.stop('spinner-1');

                })

            }
         })
    })

    $scope.computeTotalAmount = function() {
        for (var i = 0; i < $scope.productRows.length; i++) {
            $scope.totalAmount += (Math.round($scope.productRows[i].price*100)/100) * $scope.productRows[i].quantity
        }
    }

    $scope.submitForm = function(idProduct, quantity) {

        var rqtUpdateCart = {
            method : 'PUT',
            url : '/simpleUser/'+ $scope.idSimpleUser +'/product/' + idProduct +'/quantity',
            data : $.param({
                quantity: quantity
            }),
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };

        $http(rqtUpdateCart).success(function(data){
            $scope.computeTotalAmount()
            Notification.success('Quantity updated');
        })
    }

    $scope.removeRow = function(idProduct, quantity, price, row) {

        var rqtUpdateCart = {
            method : 'DELETE',
            url : '/simpleUser/'+ $scope.idSimpleUser +'/product/' + idProduct,
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };

        $http(rqtUpdateCart).success(function(data){
            $scope.totalAmount -= quantity*price
            var index = $scope.displayedCollection.indexOf(row);
            if (index !== -1) {
                $scope.displayedCollection.splice(index, 1);
            }
            Notification('Product removed from cart');
        })
    }

     $scope.displayedCollection = [].concat($scope.productRows);


})