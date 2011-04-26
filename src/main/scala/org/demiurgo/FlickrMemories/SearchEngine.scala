import scalaj.http.Http
import scala.xml._

package org.demiurgo.FlickrMemories {
  class SearchEngine {
    private def rawSearchByUserAndDateRange(userId: String,
                                            dateSpecSince: String,
                                            dateSpecUntil: String) : String = {
      return Http("http://api.flickr.com/services/rest").
                param("method",         "flickr.photos.search").
                param("api_key",        App.API_KEY).
                param("user_id",        userId).
                param("min_taken_date", dateSpecSince).
                param("max_taken_date", dateSpecUntil).asString
    }

    def searchResultXmlToPhotoSeq(xml: String) : Seq[Photo] = {
      val dom = XML.loadString(xml)
      return (dom \\ "photo").map {p => new Photo((p \ "@id").text,
                                                  (p \ "@secret").text,
                                                  (p \ "@owner").text,
                                                  (p \ "@farm").text,
                                                  (p \ "@server").text,
                                                  (p \ "@title").text)}
    }

    def searchByUserAndDateRange(userId: String, dateSpecSince: String,
                                 dateSpecUntil: String) : Seq[Photo] = {
      searchResultXmlToPhotoSeq(rawSearchByUserAndDateRange(userId,
                                                            dateSpecSince,
                                                            dateSpecUntil))
    }
  }
}
