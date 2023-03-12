/* Initial beliefs and rules */

/* Initial goals */

!drink(beer).   

/* Plans */

+!drink(beer) : not has(owner,beer) & not asked(beer) & not threwBeerCan <-
    .println("Owner no tiene cerveza.");
    !get(beer);
    !drink(beer).
+!drink(beer) : not has(owner,beer) & not asked(beer) & threwBeerCan <-
    !throwBeer;
    .println("Owner ha acabado la cerveza y la ha lanzado.");
    -threwBeerCan;
    !drink(beer).
+!drink(beer) : not has(owner,beer) & asked(beer) <- 
    .println("Owner está esperando una cerveza.");
    .wait(5000);
    !drink(beer).
+!drink(beer) : has(owner,beer) & asked(beer) <-
    .println("Owner va a empezar a beber cerveza.");
    -asked(beer);
    sip(beer);
    !drink(beer).
+!drink(beer) : has(owner,beer) & not asked(beer) <-
    sip(beer);
    .println("Owner está bebiendo cerveza.");
    +threwBeerCan;
    !drink(beer).
+!drink(beer) : ~couldDrink(beer) <-
    .println("Owner ha bebido demasiado por hoy.").    
    
+!get(beer) : not asked(beer) <-
    .send(robot, achieve, bring(owner,beer));
    .println("Owner ha pedido una cerveza al robot.");
    +asked(beer).

+!throwBeer <- 
    throwCan;
    .send(robot, tell, threw(beer)).

+msg(M)[source(Ag)] <- 
    .print("Message from ",Ag,": ",M);
    +~couldDrink(beer);
    -msg(M).
