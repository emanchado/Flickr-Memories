import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import org.demiurgo.FlickrMemories._

class SearchEngineSpec extends FlatSpec with ShouldMatchers {
  val se = new SearchEngine

  "A SearchEngine" should "correctly convert an empty response" in {
    val xml = """<?xml version="1.0" encoding="utf-8" ?>
<rsp stat="ok">
<photos page="1" pages="0" perpage="100" total="0" />
</rsp>
"""
    val result = se.searchResultXmlToPhotoSeq(xml)
    result.length should equal(0)
  }

  it should "correctly convert a response with photos" in {
    val xml = """<?xml version="1.0" encoding="utf-8" ?>
<rsp stat="ok">
<photos page="1" pages="1" perpage="100" total="3">
        <photo id="4231276405" owner="24881879@N00" secret="468409240f" server="4007" farm="5" title="IMG_3407.JPG" ispublic="1" isfriend="0" isfamily="0" />
        <photo id="4232044972" owner="24881879@N00" secret="25fc90a252" server="2715" farm="3" title="IMG_3403.JPG" ispublic="1" isfriend="0" isfamily="0" />
        <photo id="4231275671" owner="24881879@N00" secret="a5ec12fd9e" server="4010" farm="5" title="IMG_3395.JPG" ispublic="1" isfriend="0" isfamily="0" />
</photos>
</rsp>
"""
    val result = se.searchResultXmlToPhotoSeq(xml)
    result.length should equal(3)

    result(0).id       should equal("4231276405")
    result(0).secret   should equal("468409240f")
    result(0).userId   should equal("24881879@N00")
    result(0).farmId   should equal("5")
    result(0).serverId should equal("4007")
    result(0).title    should equal("IMG_3407.JPG")
    result(1).id       should equal("4232044972")
    result(1).secret   should equal("25fc90a252")
    result(1).userId   should equal("24881879@N00")
    result(1).farmId   should equal("3")
    result(1).serverId should equal("2715")
    result(1).title    should equal("IMG_3403.JPG")
    result(2).id       should equal("4231275671")
    result(2).secret   should equal("a5ec12fd9e")
    result(2).userId   should equal("24881879@N00")
    result(2).farmId   should equal("5")
    result(2).serverId should equal("4010")
    result(2).title    should equal("IMG_3395.JPG")
  }
}
