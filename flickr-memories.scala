import scalaj.http.Http
import scala.xml._
// JES. US. CHRIST.
import java.util.{Calendar,Date}
import java.text.SimpleDateFormat

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

    def searchByUserAndDateRange(userId: String, dateSpecSince: String,
                                 dateSpecUntil: String) : Seq[Photo] = {
      val dom = XML.loadString(rawSearchByUserAndDateRange(userId,
                                                           dateSpecSince,
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

    def findThisWeekLastYear(referenceDate: Date) : (Date, Date) = {
      val c = Calendar.getInstance
      c.setTime(referenceDate)
      c.add(Calendar.YEAR, -1)
      while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
        c.add(Calendar.DATE, -1)
      }
      val startDate = c.getTime
      c.add(Calendar.DATE, 7)
      val endDate = c.getTime
      return (startDate, endDate)
    }

    def main(args: Array[String]) {
      val userId        = args(0)             // 24881879@N00
      var referenceDate = new Date        // today
      val formatter     = new SimpleDateFormat("yyyy-MM-dd")
      if (args.size > 1) {
        referenceDate = formatter.parse(args(1))
      }

      val engine = new SearchEngine()

      val (dateSince, dateUntil) = findThisWeekLastYear(referenceDate)
      val dateSinceString = formatter.format(dateSince)
      val dateUntilString = formatter.format(dateUntil)
      println("<!DOCTYPE html>")
      println("<head><title>Flickr pictures from " + dateSince + " until " +
              dateUntil + "</title></head>")
      println("<body>")
      for (photo <- engine.searchByUserAndDateRange(userId, dateSinceString, dateUntilString)) {
        println("<a href=\"" + photo.pageUrl + "\">")
        println("<img src=\"" + photo.imageUrl + "\" />")
        println("</a>")
      }
      println("</body>")
    }
  }
}
