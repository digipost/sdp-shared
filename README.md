[![Build Status](https://travis-ci.org/digipost/sdp-shared.svg?branch=master)](https://travis-ci.org/digipost/sdp-shared/)

Sikker Digital Post Shared
==========================

Inneholder felleskomponenter for bruk til sending av Sikker Digital Post fra det offentlige.

Bygg
----

Etter _git clone ..._, må du kjøre 

    git submodule init 

og deretter

    git submodule update


Release
-------

Kjør

    mvn release:prepare

Og deretter

    mvn release:perform


For å tilgjengeliggjøre artefaktet i maven central

- Logg inn på sonatype med Digipost-brukeren
- Velg *Staging Repositories*
- Finn frem til *nodigi-xxxx* nederst i listen
- Velg *Content*, ekspander og verifiser at ting ser ut som forventet
- Velg *Close* og *Confirm*
- *Refresh* til status er *Closed*
- Velg *Release* og *Confirm*

