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
		const skipCount = pageOrDefault * countPerPage;

		const arr = await this.postsCollection
			.find({
				// TODO учесть timestamp
			})
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