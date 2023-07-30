# ProjectMathematicalOptimization
Repository contente il codice valido per il progetto dell'esame di Mathematical Optimization del professor Lorenzo Castelli.

Articolo scelto: Timetable coordination in a rail transit network with time-dependent passenger demand

Autori: Jiateng Yin, Andrea D’Ariano, Yihui Wang, Lixing Yang, Tao Tang 

L'implementazione è statta realizzata in Java, viene utilizzata la libreria Java Apache Commons Math per la parte di definizione del modello matematico e per la risoluzione dei problemi di ottimizzazione.

Viene fornito un esempio di dati per la creazione delle liste di percosi e stazioni. I file cvs presentati sono gli stessi che mi sono stati forniti dall'autore Jiateng Yin, per l'effettuazione degli esperimenti numerici (saranno presentati nel PPT il giorno dell'orale) ho usato anche altri dati di mia invenzione.

Nella cartella problemEntities definiamo i 3 oggetti principali, ovvero line, path e station secondo le caratteristiche presentate nel paper e dai dati ricevuti da uno degli autori del paper.

Il file Util.java è composto da un insieme di metodi statici per la creazione/definizione di vari elementi ed insiemi necessari per il modello e per le metodologie risolutive.

Il file ModelUtil.java contiene 2 metodi statici per la generazione dei parametri b ed n descritti nelle equazioni 22 e 30 del paper.

I file RepeirOperator.java e DestroyOperator.java definiscono gli oggetti omonimi per l'aggiornamento dei punteggi, pesi e probabilità, inoltre forniscono dei metodi statici per l'esecuzione effettiva degli operatori.

Il file Model.java definisce il modello matematico L-CTT presentato nell'equazione 22 e i metodi risolutivi dell'equazioni 23 e 30.

Il file Alns.java definisce la metodologia risolutiva ALNS, anlogalmente per il file Daln.java, il quale estende la classe Alns e aggiunge il metodo per l'esecuzione dell'inner loop.

Vengono anche forniti 2 file Main uno per l'esecuzione di Alns e uno per l'esecuzione di Dalns.

Nel caso si decidesse di provare il codice, va cambiata la stringa di input nelle righe 47 e 49 di Dalns.java con la stringa del percorso dove sono memorizzati i csv con i dati di input.
