/* Initial beliefs and rules */

// Identificador de la última orden entregada
last_order_id(1).

// Record of remaining stock
stock(beer, 3).

/* Initial goals */

!deliverProduct.

/* Plans */

+!order(beer, Qtd)[source(Ag)] <- 
    +orderFrom(Ag, beer, Qtd);
    .println("Pedido de ", Qtd, " cervezas recibido de ", Ag).

+!deliverProduct : last_order_id(N) & orderFrom(Ag, Product, Qtd) & stock(Product, S) & S >= Qtd <-
    OrderId = N + 1;
    -+last_order_id(OrderId);
    deliver(Product, Qtd);
    -+stock(Product,S - Qtd);
    .send(Ag, tell, delivered(Product, Qtd, OrderId));
    -orderFrom(Ag, Product, Qtd);
    !deliverProduct.
+!deliverProduct : last_order_id(N) & orderFrom(Ag, Product, Qtd) & stock(Product, S) & S < Qtd <-
    .println("Cannot fulfill order of", Qtd, " ", Product);
    .send(Ag, tell, stockRemaining(Product, S));
    -orderFrom(Ag, Product, Qtd);
    !deliverProduct.
+!deliverProduct <- !deliverProduct.
