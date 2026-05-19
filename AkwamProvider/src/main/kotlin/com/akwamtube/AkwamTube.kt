package com.akwamtube

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.Jsoup

class AkwamTube : MainAPI() {
    override var mainUrl = "https://as.akwam.tube"
    override var name = "Akwam Tube"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = "$mainUrl/l1/?page=$page"
        val response = app.get(url)
        val document = Jsoup.parse(response.text)
        
        val items = document.select("div.movie-card, article.movie").mapNotNull { element ->
            val title = element.select("h3.title, h2.movie-title").text().trim()
            val poster = element.select("img").attr("src")
            val link = element.select("a").attr("href")
            
            if (title.isNotEmpty() && link.isNotEmpty()) {
                newMovieSearchResponse(title, link, TvType.Movie) {
                    this.posterUrl = poster
                }
            } else null
        }
        
        return newHomePageResponse(request.name, items, hasNext = true)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/search/?q=${query.encodeUri()}"
        val response = app.get(url)
        val document = Jsoup.parse(response.text)
        
        return document.select("div.movie-card, article.movie").mapNotNull { element ->
            val title = element.select("h3.title, h2.movie-title").text().trim()
            val poster = element.select("img").attr("src")
            val link = element.select("a").attr("href")
            
            if (title.isNotEmpty() && link.isNotEmpty()) {
                newMovieSearchResponse(title, link, TvType.Movie) {
                    this.posterUrl = poster
                }
            } else null
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val response = app.get(url)
        val document = Jsoup.parse(response.text)
        
        val title = document.select("h1.movie-title, h1.title").text().trim()
        val poster = document.select("img.poster, meta[property='og:image']").attr("content")
        val plot = document.select("div.description, p.plot").text().trim()
        
        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            this.posterUrl = poster
            this.plot = plot
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        
        val response = app.get(data)
        val pageContent = response.text
        
        // البحث عن روابط m3u8
        Regex("""https?://[^"'\s]+\.m3u8[^"'\s]*""").findAll(pageContent).map { it.value.trim() }
            .distinct().forEach { url ->
                callback(ExtractorLink(
                    source = name, name = "${name} HLS", url = url,
                    referer = mainUrl, quality = Qualities.Unknown.value,
                    isM3u8 = true, headers = mapOf("Referer" to mainUrl)
                ))
            }
            
        // البحث عن روابط mp4
        Regex("""https?://[^"'\s]+\.mp4[^"'\s]*""").findAll(pageContent).map { it.value.trim() }
            .distinct().forEach { url ->
                callback(ExtractorLink(
                    source = name, name = "${name} MP4", url = url,
                    referer = mainUrl, quality = Qualities.Unknown.value,
                    isM3u8 = false, headers = mapOf("Referer" to mainUrl)
                ))
            }
            
        return true
    }
}
