app.controller('listProduct', function($scope, $filter, $http, $window, usSpinnerService, Notification) {

    $scope.productRows = []
    $scope.product = {}

    angular.element(document).ready(function() {

        var rqtProduct = {
            method : 'GET',
            url : '/product/available',
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };

        var rqtKindOfUser = {
            method : 'GET',
            url : '/kindOfUser',
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };

        usSpinnerService.spin('spinner-1');
        $scope.startcounter++;

        $http(rqtProduct).success(function(data){
            var xhr = new XMLHttpRequest();
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
                    price : (Math.round(data[i].price*100)/100) + "€",
                    quantity : data[i].quantity,
                    description : data[i].description
                });
            }
            usSpinnerService.stop('spinner-1');
         })

         $http(rqtKindOfUser).success(function(data){
            if (data.kindOfUser == "Simple User") {
                $scope.productBasket = []
                $scope.cartMode = true
                $scope.idSimpleUser = data.id
                var rqtBasket = {
                    method : 'GET',
                    url : '/simpleUser/' + data.id + '/product',
                    headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
                };

                $http(rqtBasket).success(function(data){
                    for (var i = 0; i < data.length; i++) {
                        $scope.productBasket.push(data[i].product.id)
                    }
                })
            }
         })
    })

    $scope.submitForm = function(idProduct) {
        var rqtAddCart = {
            method : 'POST',
            url : '/simpleUser/'+ $scope.idSimpleUser +'/product/' + idProduct,
            data : $.param({
                quantity: $scope.quantityCart
            }),
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };

        var rqtUpdateCart = {
            method : 'PUT',
            url : '/simpleUser/'+ $scope.idSimpleUser +'/product/' + idProduct +'/quantity',
            data : $.param({
                quantity: $scope.quantityCart
            }),
            headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        };

        if ($scope.productBasket.indexOf(idProduct) >= 0) {
            $http(rqtUpdateCart).success(function(data){
                Notification.success('Product updated into the cart');
            })
        }
        else {
            $http(rqtAddCart).success(function(data){
                Notification.success('Product added into the cart');
                $scope.productBasket.push({
                    id : idProduct
                })
            })
        }
    }

    $scope.show = function(id) {
        $scope.quantityCart = 1
        $scope.product.id = $scope.productRows[id].id
        $scope.product.name = $scope.productRows[id].name
        $scope.product.image = $scope.productRows[id].image
        $scope.product.price = $scope.productRows[id].price
        $scope.product.quantity = $scope.productRows[id].quantity
        $scope.product.seller = $scope.productRows[id].seller
        $scope.product.description = $scope.productRows[id].description
        $('#modal-product').modal();
        $('#modal-product').modal('show');
    }

     $scope.displayedCollection = [].concat($scope.productRows);


})