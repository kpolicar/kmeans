# K-means Clustering, Klemen Poličar

Zdravo.
To je prva oddaja projektne naloge za implementacijo **K-means clustering** algoritma.

Razred *KMeansPlusPlus*, torej sama implementacija algoritma, ni moje delo.
Vzel sem jo namreč iz interneta.
Nisem imel zadosti časa da bi zadevo sam spisal, vendar jo imam namen spisat pred oddajo
končnega projekta.

Kodo vseeno popolnoma razumem. Mogoče se sprašujete, kaj pomeni ta *epsilon* - to
se sprašujete verjetno, ker ponavadi (odvisno od zbirke podatkov) procesiramo algoritem
do popolnega konvergiranja. Torej, do trenutka, ko se grupiranje (clustering) podatkov
ne spreminja več. *Epsilon* v tej implementaciji pomeni parameter, ki pove
algoritmu, pri koliko manjši spremembi od prejšnega grupiranja naj se algoritem ustavi.
Torej, če pride algoritem do točke, ko se skupine spreminjajo le še zelo malo, potem 
lahko predčasno ustavimo. Ne potrebujemo popolnega konvergiranja
"funkcije napake" (error function) do 0.
Upam, da sem približno jasno napisal.

*Epsilon* je vrednost med 0 in 1. Če jo nastavite na 0, se bo algoritem izvajal
do popolne konvergence.