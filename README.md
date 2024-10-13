# Project GlobalWaves

## Overview
This project implements many commands, each one of them having a separate
thought process, so I will explain each one of them and how they link
throughout the whole project, as they are also implemented with some design
patterns.

## Design Patterns used:
* Singleton to handle the single instance of the Admin (I've also used this one
in the previous project)
* Strategy to handle the wrapped command
* Observer for handling the notifications and subscrie command
* Command for navigating through the pages

## The commands and their implementation
* wrapped: (disclaimer: I've tried to implement this one with the STRATEGY design
pattern, but I have encountered some difficulties in handling the number of 
listeners, but the design pattern is implemented). The goal was to creat a contract
between all the statistics generation strategies.


* subscribe (OBSERVER DESIGN PATTERN): when on an artist page, the user is added to 
the observes list, if not
already, meaning if there is no subscription, it is added to that list and if else,
the opposite happens. This is where the observes is either attached or detached from
to subject's "action", meaning if it wants or not to get updated with info about the
artist.

* notifications (OBSERVER DESIGN PATTERN): a new notification is created when the subject
does a new action, hence the user/observer is notified about each action of the subject.
The Notifications class helps with the implementation of this command, so the new 
notification command was added in the right methods, when the subject/artist completes
a new action. This one was implemented in the User class, having also an update method,
that is called in the Host and Artist classes, when iterating through the users with the
purpose of notifying them about the new action.

* buyMerch & seeMerch: this command was quite easy to implement, as each time a user is
on an artist page, it can buy the merch from that artist, adding it to the list of
merchandise, which comes in handy for the seeMerch command. If bought, the specific product
is added to the boughMerch list, so it can be displayed within the other command. I used
streams to filter the lists more efficiently.

* previousPage & nextPage (COMMAND DESIGN PATTERN): for these commands there was implemented
an interface with a method that executes the navigation and the afferent classes. Because each
page behaviour is encapsulated in a different class, it makes it way easier to work with the
code. Each execute method called the method from the user class, as the user is the one who
navigates through everything. The previousPage method retains a historyIndex that is
decreased each time the user goes back a page, and the navigationHistory list of pages is also
set back with one. Same thought process was used for the nextPage method.

* updateRecommendations: for each type of recommendation, there is created a specific method in
the Admin class.
       songs : the genre of the playing song is set by iterating through the library entry,
       and the songs that amtch it are added to an array so the generation of a random song 
       could work as required. The song is then added to the array of recommendations for the
       user, so it can be displayed in the HomePage.

       fans playlist : this method needed 2 additional ones, one that returned the number of
       likes of that specific artist with the help of a stream, and the other one that returned
       the list of the top fans of the artist, with the help of the method before. This one
       iterates through all the users and counts their likes, so the biggest fans could be added
       to the array of recommendations. Then the main method creates a new playlist and add it to
       the array of recommendations.

       random_playlist: this main method also used to helpful ones, first one that returns a map
       with the sorted genres and their popularity with a stream, and the other one that creates 3
       listst of songs that are added in a combined list, which then eliminates the dupliates by 
       using a set and orting them. This final list of songs is used in the main method, when the
       random playlist based on the top genres is created and added to the array of recommendations.

* loadRecommendations: this was solely implemented in the commandRunner, by imitating the thought
process of the load command. Depending on the last type of recommendation given to the user, the source
is set to either a random song, random playlist or fan playlist, loading then the same source with the 
audiofile that's required.

## Challenges
Being my first time working with design patterns, it was difficult at first, but then I understood their
importance, as they make the code more extensive and easier to understand.
    
