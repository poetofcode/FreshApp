const ObjectId = require("mongodb").ObjectId;
const crypto = require('crypto');
const { utils } = require('../utils');
const axios = require('axios');

const mockFeed = '{ "posts":[{"title":"DTF: Запоминаем:","image":"https://leonardo.osnova.io/f32c79fd-a530-5af8-bc94-70ab84be851b/-/scale_crop/592x/-/format/png/","link":"http://dtf.ru/u/511248-lyuser-de-montefio-zezezetovich/3586646-zapominaem","commentsCount":""},{"title":"DTF: Просто напоминаю, как могут выглядеть игры без навороченных технологий","image":"https://leonardo.osnova.io/6e8f6cf6-1c91-5133-9f9e-f07804e479ca/-/scale_crop/592x/-/format/png/","link":"https://dtf.ru/games/3588503-prosto-napominayu-kak-mogut-vyglyadet-igry-bez-navorochennyh-tehnologii","commentsCount":""},{"title":"DTF: Костюм простой, зато что творит...","image":null,"link":"https://dtf.ru/u/453373-dwwr/3587237-kostyum-prostoi-zato-chto-tvorit","commentsCount":""},{"title":"DTF: Эх...","image":null,"link":"https://dtf.ru/mobile/3588556-eh","commentsCount":""},{"title":"DTF: Скончался актёр Джин Хэкмен — звезда «Французского связного» и «Непрощённого»","image":"https://leonardo.osnova.io/fb193575-f232-5879-95db-d97c9048a2e5/-/scale_crop/592x/-/format/png/","link":"https://dtf.ru/life/3589359-skonchalsya-akter-dzhin-hekmen-zvezda-francuzskogo-svyaznogo-i-neproshennogo","commentsCount":""},{"title":"DTF: «Этот мир умирает»: трейлер сериального перезапуска «Этерны» от «Кинопоиска»","image":null,"link":"https://dtf.ru/cinema/3589217-etot-mir-umiraet-treiler-serialnogo-perezapuska-eterny-ot-kinopoiska","commentsCount":""},{"title":"DTF: Как я CRPG познавал: Часть 23. Санитары подземелий, дилогия","image":"https://leonardo.osnova.io/f858ed54-0876-5d15-a56c-b16be30fd30e/-/scale_crop/592x/-/format/png/","link":"https://dtf.ru/games/2803364-kak-ya-crpg-poznaval-chast-23-sanitary-podzemelii-dilogiya","commentsCount":""},{"title":"DTF: Саундтрек для отменённой игры про Чудо-женщину от Monolith писал Йеспер Кюд","image":"https://leonardo.osnova.io/785e3d50-f477-51bd-80b0-072a1a21ca92/-/scale_crop/592x/-/format/png/","link":"https://dtf.ru/gameindustry/3589036-saundtrek-dlya-otmenennoi-igry-pro-chudo-zhenshinu-ot-monolith-pisal-iesper-kyud","commentsCount":""},{"title":"DTF: Пикча с сабреддита, где иностранцы изучают русский язык. Человек спрашивает, что значит на ключе.Точного ответа так никто и не дал, но есть достойный вариант.","image":"https://leonardo.osnova.io/d0162395-1ebb-505e-9381-c6b78ed0de9e/-/scale_crop/592x/-/format/png/","link":"https://dtf.ru/u/1731513-vederko-krabov/3588690-pikcha-s-sabreddita-gde-inostrancy-izuchayut-russkii-yazyk-chelovek-sprashivaet-chto-znachit-na-klyuchetochnogo-otveta-tak-nikto-i-ne-dal-no-est-dostoinyi-variant","commentsCount":""},{"title":"DTF: В Steam расширяют число игр в рамках «Предложения дня» — с четырёх до шести","image":"https://leonardo.osnova.io/5d449415-f9dc-5886-811c-7385e040ea48/-/scale_crop/592x/-/format/png/","link":"https://dtf.ru/steam/3588934-v-steam-rasshiryayut-chislo-igr-v-ramkah-predlozheniya-dnya-s-chetyreh-do-shesti","commentsCount":""},{"title":"DTF: Gamer till The End","image":null,"link":"https://dtf.ru/u/286910-kr0m/3589008-gamer-till-the-end","commentsCount":""},{"title":"DTF: СМИ: Кристофер Нолан изъявлял желание снять фильм про Джеймса Бонда после «Довода»","image":"https://leonardo.osnova.io/e9616231-69f0-5c48-afbb-0a8b0c82bb88/-/scale_crop/592x/-/format/png/","link":"https://dtf.ru/cinema/3588721-smi-kristofer-nolan-izyavlyal-zhelanie-snyat-film-pro-dzheimsa-bonda-posle-dovoda","commentsCount":""}] }';
const mockDashboard = `
	{
		"categories": [],
		"sources": [
			"habr",
			"dtf",
			"lenta",
			"3dnews"
		]
	}
`

class GrabberRepository {

	constructor(context) {
		this.context = context;
	}

	async fetchTotalFeed() {
		const sources = (await this.fetchDashboard()).sources;
		const totalPosts = await Promise.all(sources.map(source => this.fetchFeedBySource(source)));
	    return totalPosts.flat();
	}

	async fetchDashboard() {
		return JSON.parse(mockDashboard);
	}


	async fetchFeedBySource(source) {
		const url = this.makeParserUrl(`v2/feed/${source}`);

		try {
			const response = await axios({
	          method: 'get',
	          url: url,
	        });

			const responseData = response.data;

			if (responseData.result && responseData.result.result == 'ok') {
				const result = responseData.result;
				console.log(`GrabberRepository, parsing of '${source}' success: ${result.posts.length} posts parsed`);
				return result.posts.map(post => {
					const newPost = post;
					newPost.source = source;
					return newPost;
				}); 
			}

		} catch (error) {
			console.log(`GrabberRepository ERROR, method: fetchFeedBySource(${source}), exception: ${error}`);
		}

		return [];
	} 


	makeParserUrl(path) {
		const parser = this.context.config.parser;
		return `${parser.path}{place_to_insert}?debug_key=${parser.key}`.replace(
			'{place_to_insert}',
			path
		)
	}


}

exports.create = (context) => new GrabberRepository(context);