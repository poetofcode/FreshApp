const ObjectId = require("mongodb").ObjectId;
const crypto = require('crypto');
const { utils } = require('../utils');

const countPerPage = 5;


class FeedRepository {

	constructor(context) {
		this.context = context;
		this.postsCollection = context.getDb().collection('posts');
	}

	async saveFeed(posts) {
		let bulkTags = [];
		
		posts.forEach(post => {
			post.createdAt = new Date();
			bulkTags.push({
				updateOne: {
					filter: { link: post.link },
					update: { $set: post },
					upsert: true
				}
			});
		});

        await this.postsCollection.bulkWrite(bulkTags);
	}


	async getFeed(sources, page, timestampFrom) {
		const pageOrDefault = page || 0;
		const sourcesOrDefault = sources || [];
		const skipCount = pageOrDefault * countPerPage;
		let timestampOrDefault = 0;		// Timestamp in milliseconds
		if (timestampFrom > 0) {
		 	timestampOrDefault = timestampFrom * 1;
		} else {
			timestampOrDefault = Date.now();
		}

		console.log(`TS or default: ${timestampOrDefault}`)

		const arr = await Promise.all(
			sourcesOrDefault.map(async (source) => {		
				let query = {};
				query.source = source;
				query.createdAt = { $lte: new Date(timestampOrDefault) };

				return await this.postsCollection
					.find(query)
					.sort({ _id: -1, createdAt: -1 })
					.limit(countPerPage /* countPerPage + 1 */) 
					.skip(skipCount)
					.toArray();
			})
		);



		return {
			posts: arr.flat(),
			isNextAllowed: false,
			page: pageOrDefault,
			timestamp: timestampOrDefault,
		};
	} 

}

exports.create = (context) => new FeedRepository(context);