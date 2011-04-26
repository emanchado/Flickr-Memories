import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import org.demiurgo.FlickrMemories._

class PhotoSpec extends FlatSpec with ShouldMatchers {
  val p = new Photo("5317414257", "6215a6a128", "24881879@N00",
                    "6", "5041", "untitled")

  "A Photo" should "correctly fetch the description from a Flickr API response" in {
    val xml = """<?xml version="1.0" encoding="utf-8" ?>
<rsp stat="ok">
<photo id="5317414257" secret="6215a6a128" server="5041" farm="6" dateuploaded="1294011137" isfavorite="0" license="1" safety_level="0" rotation="0" originalsecret="5a24001561" originalformat="jpg" views="42" media="photo">
        <owner nsid="24881879@N00" username="Esteban Manchado" realname="" location="" iconserver="92" iconfarm="1" />
        <title>IMG_4261.JPG</title>
        <description>John Lennon sand sculpture in Las Canteras beach, in Las Palmas de Gran Canaria.</description>
        <visibility ispublic="1" isfriend="0" isfamily="0" />
        <dates posted="1294011137" taken="2010-12-22 19:15:59" takengranularity="0" lastupdate="1294011960" />
        <editability cancomment="0" canaddmeta="0" />
        <publiceditability cancomment="1" canaddmeta="0" />
        <usage candownload="1" canblog="0" canprint="0" canshare="0" />
        <comments>0</comments>
        <notes />
        <tags>
                <tag id="3440252-5317414257-2875" author="24881879@N00" raw="sand" machine_tag="0">sand</tag>
                <tag id="3440252-5317414257-308" author="24881879@N00" raw="sculpture" machine_tag="0">sculpture</tag>
                <tag id="3440252-5317414257-350934" author="24881879@N00" raw="canteras" machine_tag="0">canteras</tag>
                <tag id="3440252-5317414257-704" author="24881879@N00" raw="beach" machine_tag="0">beach</tag>
                <tag id="3440252-5317414257-135" author="24881879@N00" raw="las" machine_tag="0">las</tag>
                <tag id="3440252-5317414257-160692" author="24881879@N00" raw="palmas" machine_tag="0">palmas</tag>
                <tag id="3440252-5317414257-20046" author="24881879@N00" raw="gran" machine_tag="0">gran</tag>
                <tag id="3440252-5317414257-65347" author="24881879@N00" raw="canaria" machine_tag="0">canaria</tag>
                <tag id="3440252-5317414257-1443" author="24881879@N00" raw="john" machine_tag="0">john</tag>
                <tag id="3440252-5317414257-33894" author="24881879@N00" raw="lennon" machine_tag="0">lennon</tag>
                <tag id="3440252-5317414257-19751" author="24881879@N00" raw="beatles" machine_tag="0">beatles</tag>
        </tags>
        <urls>
                <url type="photopage">http://www.flickr.com/photos/emanchado/5317414257/</url>
        </urls>
</photo>
</rsp>"""
    p.extractDescriptionFromFlickResponse(xml) should equal("John Lennon sand sculpture in Las Canteras beach, in Las Palmas de Gran Canaria.")
  }
}
