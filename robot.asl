/* Initial beliefs and rules */

// Initially, I believe that there is some beer in the fridge.
available(beer,fridge).

// My owner should not consume more than N beers a day.
limit(beer,7).

too_much(B) :-
   .date(YY,MM,DD) &
   .count(consumed(YY,MM,DD,_,_,_,B),QtdB) &
   limit(B,Limit) &
   QtdB >= Limit.

/* Initial goals */

!bringBeer.

/* Plans */

+!bring(owner, beer) <-
    +asked(beer).

+!bringBeer : not asked(beer) & not healthMsg(_) <- 
    .wait(2000);
    .println("Robot esperando la petición de Owner.");
    !bringBeer.
+!bringBeer : asked(beer) & not healthMsg(_) & threw(beer)[source(A)] <- 
    .wait(1000);
    .println("Voy a por la lata");
    !go_at(robot,can);
    .wait(1000);
    pickUpTrash;
    .println("Tirando la basura antes de ir a la nevera");
    !go_at(robot,bin);
    .wait(1000);
    -threw(beer)[source(A)];
    !bringBeer.
+!bringBeer : asked(beer) & not healthMsg(_) & not threw(beer) <- 
    .println("Owner me ha pedido una cerveza.");
    !go_at(robot,fridge);
    !take(fridge,beer);
    !go_at(robot,owner);
    !hasBeer(owner);
    .println("Ya he servido la cerveza y elimino la petición.");
    .abolish(asked(Beer));
    !bringBeer.
+!bringBeer : healthMsg(_) <- 
    !go_at(robot,base);
    .println("El Robot descansa porque Owner ha bebido mucho hoy.").

+!take(fridge, beer) : not too_much(beer) <-
    .println("El robot está cogiendo una cerveza.");
    !check(fridge, beer).
+!take(fridge,beer) : too_much(beer) & limit(beer, L) <-
    .concat("The Department of Health does not allow me to give you more than ", L," beers a day! I am very sorry about that!", M);
    -+healthMsg(M).

+!check(fridge, beer) : not ordered(beer) & available(beer,fridge) <-
    .println("El robot está en el frigorífico y coge una cerveza.");
    .wait(1000);
    open(fridge);
    .println("El robot abre la nevera.");
    get(beer);
    .println("El robot coge una cerveza.");
    close(fridge);
    .println("El robot cierra la nevera.").
+!check(fridge, beer) : not ordered(beer) & not available(beer,fridge) <-
    .println("El robot está en el frigorífico y hace un pedido de cerveza.");
    !order(beer, supermarket);
    !check(fridge, beer).
+!check(fridge, beer) <-
    .println("El robot está esperando ................");
    .wait(5000);
    !check(fridge, beer).

+!order(Product, Supermarket) : not ordered(Product) <-
    .println("El robot ha realizado un pedido al supermercado.");
    !go_at(robot,pickup);
    .println("El robot va a la ZONA de ENTREGA.");
    .send(Supermarket, achieve, order(Product,3));
    +ordered(Product).
+!order(Product, Supermarket).

+!hasBeer(owner) : not too_much(beer) <-
    hand_in(beer);
    .println("He preguntado si Owner ha cogido la cerveza.");
    ?has(owner,beer);
    .println("Se que Owner tiene la cerveza.");
    // remember that another beer has been consumed
    .date(YY,MM,DD); .time(HH,NN,SS);
    +consumed(YY,MM,DD,HH,NN,SS,beer).
+!hasBeer(owner) : too_much(beer) & healthMsg(M) <- 
    //.abolish(msg(_));
    .send(owner,tell,msg(M)).

+!go_at(robot,P) : at(robot,P) <- true.
+!go_at(robot,P) : not at(robot,P)
  <- move_towards(P);
     !go_at(robot,P).

// when the supermarket makes a delivery, try the 'has' goal again
+delivered(beer,_Qtd,_OrderId)[source(supermarket)] <- 
    -ordered(beer);
    +available(beer,fridge);
    .wait(1000);
    !go_at(robot,fridge).

+stockRemaining(Product,N)[source(supermarket)] 
   <- .print("Supermarket can't attend the order due to insufficient stock (",N,") of ", Product).

// When the fridge is opened, the beer stock is perceived
// and thus the available belief is updated
+stock(beer,0) : available(beer,fridge) <-
    -available(beer,fridge).
+stock(beer,N) : N > 0 & not available(beer,fridge) <-
    -+available(beer,fridge).

+?time(T) : true
  <-  time.check(T).
