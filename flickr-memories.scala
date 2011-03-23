import scalaj.http.Http
import scala.xml._
// JES. US. CHRIST.
import java.util.{Calendar,Date}
import java.text.SimpleDateFormat
// Template system
import biz.source_code.miniTemplator.MiniTemplator
// Configuration (in YAML; configgy is just annoying)
import org.yaml.snakeyaml.Yaml
// Mail
import javax.mail._
import javax.mail.internet.{InternetAddress,MimeMessage}


package FlickrMemories {
  class Configuration(var file: String = null) {
    var configuration : java.util.LinkedHashMap
                            [String, java.util.LinkedHashMap[String,
                                                             String]] =
                                                               new java.util.LinkedHashMap

    if (file != null)
      configuration = readFromFile(file)

    def readFromFile(file: String) : java.util.LinkedHashMap[String, java.util.LinkedHashMap[String, String]] = {
      readFromString(scala.io.Source.fromFile(file).mkString)
    }

    def readFromString(yamlString: String) : java.util.LinkedHashMap[String, java.util.LinkedHashMap[String, String]] = {
      val yaml = new Yaml
      return yaml.load(yamlString).
                    asInstanceOf[java.util.LinkedHashMap
                                 [String, java.util.LinkedHashMap[String,
                                                                  String]]]
    }

    def get(key: String, subkey: String) : String = {
      configuration.get(key).get(subkey)
    }
  }

  class Photo(var id: String, var secret: String, var userId: String,
              var farmId: String, var serverId: String, var title: String) {
    var _description : String = null

    def imageUrl : String = {
      return "http://farm" + farmId + ".static.flickr.com/" +
                serverId + "/" + id + "_" + secret + ".jpg"
    }

    def pageUrl : String = {
      return "http://www.flickr.com/photos/" + userId + "/" + id
    }

    def extractDescriptionFromFlickResponse(xml: String) : String = {
      val dom = XML.loadString(xml)
      return ((dom \\ "PHOTO")(0) \ "DESCRIPTION").text
    }

    def fetchPhotoDescription : String = {
      val xml = Http("http://api.flickr.com/services/rest").
                  param("method",         "flickr.photos.getinfo").
                  param("api_key",        App.API_KEY).
                  param("photo_id",       id).asString
      return extractDescriptionFromFlickResponse(xml)
    }

    def description : String = {
      if (_description == null) {
        _description = fetchPhotoDescription
      }

      return _description
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

    def searchResultXmlToPhotoSeq(xml: String) : Seq[Photo] = {
      val dom = XML.loadString(xml)
      return (dom \\ "PHOTO").map {p => new Photo((p \ "@id").text,
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

  object App {
    val API_KEY       = "30c195ccce757cd281132f0bef44de0d"
    val TEMPLATE_PATH = "template.html"

    def findThisWeekInPastYear(referenceDate: Date, numberOfYears: Int = 1) : (Date, Date) = {
      val c = Calendar.getInstance
      c.setTime(referenceDate)
      c.add(Calendar.YEAR, -1 * numberOfYears)
      while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
        c.add(Calendar.DATE, -1)
      }
      val startDate = c.getTime
      c.add(Calendar.DATE, 7)
      val endDate = c.getTime
      return (startDate, endDate)
    }

    def htmlForPhotos(photos: Seq[Photo], dateSince: Date, dateUntil: Date) : String = {
      val t = new MiniTemplator(TEMPLATE_PATH)
      val niceDateFormatter = new SimpleDateFormat("MMM d")
      val yearDateFormatter = new SimpleDateFormat("yyyy")
      t.setVariable("dateSince", niceDateFormatter.format(dateSince))
      t.setVariable("yearSince", yearDateFormatter.format(dateSince))
      t.setVariable("dateUntil", niceDateFormatter.format(dateUntil))
      for (photo <- photos) {
        t.setVariable("pageUrl",     photo.pageUrl)
        t.setVariable("imageUrl",    photo.imageUrl)
        t.setVariable("title",       photo.title)
        t.setVariable("description", photo.description)
        t.addBlock("photo")
      }
      return t.generateOutput
    }

    def mail(to: String, subject: String, body: String) {
      val props = System.getProperties()
      val conf  = new Configuration("config.yml")
      props.put("mail.smtp.host", conf.get("Mail", "host"))
      props.put("mail.smtp.port", conf.get("Mail", "port"))

      val session = Session.getDefaultInstance(props, null)
      val message = new MimeMessage(session)
      message.setFrom(new InternetAddress(conf.get("Mail", "from")))
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to))
      message.setSubject(subject)
      message.setContent(body, "text/html; charset=\"utf-8\"")
      Transport.send(message)
    }

    def main(args: Array[String]) {
      val userId           = args(0)         // 24881879@N00
      var referenceDate    = new Date        // today by default
      var mailRecipient    = ""
      val rfc3339Formatter = new SimpleDateFormat("yyyy-MM-dd")
      if (args.size > 1) {
        referenceDate = rfc3339Formatter.parse(args(1))
      }
      if (args.size > 2) {
        mailRecipient = args(2)
      }

      val engine = new SearchEngine()

      var years = 1
      var picsFound = false
      do {
        val (dateSince, dateUntil) = findThisWeekInPastYear(referenceDate,
                                                            years)
        val dateSinceString = rfc3339Formatter.format(dateSince)
        val dateUntilString = rfc3339Formatter.format(dateUntil)

        val photos = engine.searchByUserAndDateRange(userId, dateSinceString, dateUntilString)
        if (photos.length > 0) {
          picsFound = true
          val output = htmlForPhotos(photos, dateSince, dateUntil)
          if (mailRecipient != "") {
            mail(mailRecipient,
                 "FlickMemories " + dateSinceString + " - " + dateUntilString,
                 output)
          } else {
            println(output)
          }
        }
        years += 1
      } while (years < 6 && !picsFound)

      if (! picsFound && mailRecipient == "") {
        println("Didn't find any photos for " + args(1))
      }
    }
  }
}
