Flickr Memories
===============

This is a *very* simple utility to fetch pictures from a week a year
ago from a given Flickr account (if no pictures are found a year ago,
previous years are attempted). It generates a static web page that you
can load in any browser to see the pictures, or alternatively it can
send that webpage as an e-mail to a chosen e-mail address. It's
written in Scala, and part of the point of writing it in the first
place was learning some Scala, so don't expect (1) idiomatic Scala
code you can learn from, or (2) a polished/well-maintained code.

BTW, I know there's already a service that does something similar,
probably much better, but as I said I wanted to learn some Scala ;-)

To run this, you'll need:

* [scalaj-http](https://github.com/scalaj/scalaj-http)
* [Scalate](http://scalate.fusesource.org/)
* [JavaMail](http://www.gnu.org/software/classpathx/javamail/javamail.html)
* [snakeyaml](http://code.google.com/p/snakeyaml/)

If you want to run the tests, you'll also need the
[ScalaTest](http://www.scalatest.org/) library. To compile, run or
test the code, you can use
[sbt](http://code.google.com/p/simple-build-tool/).

Comments, particularly about the code or the tools, more than welcome!

After compiling the source, you can execute the utility by typing
(make sure you have a correct `CLASSPATH`, like
`lib/*:target/scala_2.8.1/classes` or similar!):

    scala org.demiurgo.FlickrMemories.App <your Flickr NSID> >pictures.html

you can specify a different reference date (as opposed to "today"):

    scala org.demiurgo.FlickrMemories.App <your Flickr NSID> <yyyy-mm-dd> >pictures.html

E.g.:

    scala org.demiurgo.FlickrMemories.App 24881879@N00            >pictures.html
    scala org.demiurgo.FlickrMemories.App 24881879@N00 2010-11-13 >pictures.html

If you want to send the result via e-mail, you have to configure the
outgoing SMTP server (ie. copy `config.yml-sample` to `config.yml` and
set appropriate values for the different configuration keys). Then,
you can just specify an e-mail address as an extra parameter at the
end, like so:

    scala org.demiurgo.FlickrMemories.App 24881879@N00 2010-11-13 emanchado@demiurgo.org

[![Flattr this git repo](http://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/submit/auto?user_id=emanchado&url=https://github.com/emanchado/Flickr-Memories&title=Flickr Memories&language=en_GB&tags=github&category=software)
