eBlast
======

eBlast est un client BitTorrent capable de gérer plusieurs téléchargement en même temps. Comme vous pouvez le voir ci-dessus, il implémente diverses fonctionnalités telles que :
 * Affichage des informations concernant le torrent.
 * Téléchargement de plusieurs fichiers torrent.
 * Mettre en pause des torrents et redémarrer ceux-ci.
 * Affichage graphique des pièces en cours de téléchargement, ainsi que celles disponibles.
 * Affichage des pairs connectés ainsi que de leur disponibilités.

![eBlast](https://github.com/ThmX/epfl-ba2-ITP-eBlast/raw/master/eBlast.png "eBlast")


### Informations
![Informations](https://github.com/ThmX/epfl-ba2-ITP-eBlast/raw/master/info.png "Informations")
	\includegraphics[scale=0.5]{infos}
Toutes les informations concernant le \textit{torrent} sont résumées sur ce panneau.

### Trackers
![Trackers](https://github.com/ThmX/epfl-ba2-ITP-eBlast/raw/master/tracker.png "Trackers")
Cette onglet vous permet de savoir combien de pairs sont connectés à un \textit{tracker}, et surtout si la connexion a bien été établie.

### Peers
![Peers](https://github.com/ThmX/epfl-ba2-ITP-eBlast/raw/master/peers.png "Peers")
L'onglet \textit{Peers} permet d'afficher tous les pairs connectés, le client utilisé ainsi que leurs pourcentages de téléchargement.

### Files
![Files](https://github.com/ThmX/epfl-ba2-ITP-eBlast/raw/master/files.png "Files")
Cette onglet n'est pas utile pour le moment. Cependant, la possiblité de télécharger à l'aide de torrents multi-fichiers sera implémentée prochainement. De ce fait, il sera pratique d'afficher la liste de tous les fichiers contenua dans le fichier torrent.

### Configurations
![Configurations](https://github.com/ThmX/epfl-ba2-ITP-eBlast/raw/master/config.png "Configurations")
Il est possible de configurer les options suivantes :
 * Répertoire de téléchargement
 * Port de connexion auquels les pairs vont se connecter.
 * Le nombre maximum de pairs auquels chaque torrent peut se connecter.
 * Si le cryptage est activé.


## License

The MIT License (MIT)

Copyright (c) 2014 Thomas Denoréaz

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.