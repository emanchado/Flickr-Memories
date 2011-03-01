import scalaj.http.Http
import scala.xml._

package FlickrMemories {
  class Photo(id: String, secret: String, userId: String,
              farmId: String, serverId: String, title: String) {
    def imageUrl : String = {
      return "http://farm" + farmId + ".static.flickr.com/" +
                serverId + "/" + id + "_" + secret + ".jpg"
    }

    def pageUrl : String = {
      return "http://www.flickr.com/photos/" + userId + "/" + id
    }
  }

  class SearchEngine {
    private def rawSearchResults(userId: String, dateSpecSince: String,
                                 dateSpecUntil: String) : String = {
      return Http("http://api.flickr.com/services/rest").
                param("method",         "flickr.photos.search").
                param("api_key",        App.API_KEY).
                param("user_id",        userId).
                param("min_taken_date", dateSpecSince).
                param("max_taken_date", dateSpecUntil).asString
    }

    def listSearchResults(userId: String, dateSpecSince: String,
                          dateSpecUntil: String) : Seq[Photo] = {
      val dom = XML.loadString(rawSearchResults(userId, dateSpecSince,
                                                        dateSpecUntil))
      return (dom \\ "photo").map {p => new Photo((p \ "@id").text,
                                                  (p \ "@secret").text,
                                                  (p \ "@owner").text,
                                                  (p \ "@farm").text,
                                                  (p \ "@server").text,
                                                  (p \ "@title").text)}
    }
  }

  object App {
    val API_KEY = "30c195ccce757cd281132f0bef44de0d"

    def main(args: Array[String]) {
      val userId    = args(0)             // 24881879@N00
      val dateSince = args(1)
      val dateUntil = args(2)

      val engine = new SearchEngine()

      println("<!DOCTYPE html>")
      println("<head><title>Flickr pictures from " + dateSince + " until " +
              dateUntil + "</title></head>")
      println("<body>")
      println("<ul>")
      for (photo <- engine.listSearchResults(userId, dateSince, dateUntil)) {
        println("<a href=\"" + photo.pageUrl + "\">")
        println("<img src=\"" + photo.imageUrl + "\" />")
        println("</a>")
      }
      println("</ul>")
      println("</body>")
    }
  }
}
