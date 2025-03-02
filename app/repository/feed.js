const ObjectId = require("mongodb").ObjectId;
const crypto = require('crypto');
const { utils } = require('../utils');

const countPerPage = 10;


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
		const timestampOrDefault = timestampFrom || 0;

		let query = {};
		if (sourcesOrDefault.length > 0) {
			query.source = sourcesOrDefault[0];
		}

		if (timestampOrDefault > 0) {
			// TODO учесть timestamp
			// query.createdAt = sourcesOrDefault[0];
		}

		const arr = await this.postsCollection
			.find(query)
			.sort({ createdId: -1, _id: -1 })
			.limit(countPerPage + 1) 
			.skip(skipCount)
			.toArray();

		return {
			posts: arr,
			isNextAllowed: false,
			page: pageOrDefault,
			timestamp: 0,
		};
	} 

}

exports.create = (context) => new FeedRepository(context);