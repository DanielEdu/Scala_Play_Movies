package controllers

import javax.inject._
import models.{Movie, MovieRepository}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class MovieController @Inject()(
                                 cc: ControllerComponents,
                                 movieRepository: MovieRepository
                               ) extends AbstractController(cc) {

  implicit val serializador = Json.format[Movie]
  val logger = play.Logger.of("MovieController")   // crear el logger para los errores

  def getMovies: Action[AnyContent] = Action.async {
    movieRepository
      .getAll
      .map( movies => {
        val j = Json.obj(
          fields = "data" -> movies,
          "message" -> "Movies listed"
        )
        Ok(j)
      }).recover {
      case ex =>
        logger.debug("Falló en getMovies",ex)
        InternalServerError(s"Hubo un error: ${ex.getLocalizedMessage}")
    }
  }

  def getMovie(id: String): Action[AnyContent] = Action.async {
    movieRepository
      .getOne(id)
      .map( movie => {
        val j = Json.obj(
          fields = "data" -> movie,
          "message" -> "Movie"
        )
        Ok(j)
      }).recover {
      case ex =>
        logger.debug("Falló en getMovie",ex)
        InternalServerError(s"Hubo un error: ${ex.getLocalizedMessage}")
    }
  }

  def createMovie: Action[JsValue] = Action.async(parse.json) { request =>
    val validator = request.body.validate[Movie]

    validator.asEither match {
      case Left(error) => Future.successful(BadRequest(error.toString()))
      case Right(movie) =>
        movieRepository
          .create(movie)
          .map( movie => {
            val j = Json.obj(
              fields = "data" -> movie,
              "message" -> "Movie created"
            )
            Ok(j)
          }).recover {
          case ex =>
            logger.debug("Falló en createMovie",ex)
            InternalServerError(s"Hubo un error: ${ex.getLocalizedMessage}")
        }
    }
  }


  def updateMovie(id: String): Action[JsValue] = Action.async(parse.json) { request =>
    val validator = request.body.validate[Movie]

    validator.asEither match {
      case Left(error) => Future.successful(BadRequest(error.toString()))
      case Right(movie) =>
        movieRepository
          .update(id, movie)
          .map( movie => {
            val j = Json.obj(
              fields = "data" -> movie,
              "message" -> "Movie created"
            )
            Ok(j)
          }).recover {
          case ex =>
            logger.debug("Falló en updateMovie",ex)
            InternalServerError(s"Hubo un error: ${ex.getLocalizedMessage}")
        }
    }
  }


  def deleteMovie(id: String): Action[AnyContent] = Action.async {
    movieRepository
      .delete(id)
      .map( movie => {
        val j = Json.obj(
          fields = "data" -> movie,
          "message" -> "Movie"
        )
        Ok(j)
      }).recover {
      case ex =>
        logger.debug("Falló en deleteMovie",ex)
        InternalServerError(s"Hubo un error: ${ex.getLocalizedMessage}")
    }
  }


}
