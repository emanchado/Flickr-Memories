Flickr Memories
===============

This is a *very* simple utility to fetch pictures from a range of
dates and a given Flickr account. It's written in Scala, and part of
the point of writing it in the first place was learning some Scala, so
don't expect (1) idiomatic Scala code you can learn from, or (2) a
polished/well-maintained code.

BTW, I know there's already a service that does something similar,
probably much better, but as I said I wanted to learn some Scala ;-)

To run this, you'll need
[scalaj-http](https://github.com/scalaj/scalaj-http).

Any comments, particularly about the Scala code or nice tools I should
be using (build system, documentation, unit testing, etc.) more than
welcome!

After compiling the source, you can execute the utility by typing:

    scala FlickrMemories.App <your Flickr NSID> <date since> <date until> >pictures.html

E.g.:

    scala FlickrMemories.App 24881879@N00 2009-11 2010-01 > pictures.html
