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
		
		posts.forEach((post, idx) => {
			bulkTags.push({
				updateOne: {
					filter: { link: post.link },
					update: { 
						$set: post,
						$setOnInsert: { createdAt: new Date() }
					},
					upsert: true
				}
			});
		});

        await this.postsCollection.bulkWrite(bulkTags);
	}


	async getFeed(sources, page, timestampFrom) {
		const pageOrDefault = page || 0;
		let sourcesOrDefault = sources || [];
		if (sourcesOrDefault.length == 0) {
			sourcesOrDefault = (await this.context.repositories.grabber.fetchDashboard()).sources;
			console.log("Default sources:");
			console.log(sourcesOrDefault);
		}
		const skipCount = pageOrDefault * countPerPage;
		let timestampOrDefault = 0;		// Timestamp in milliseconds
		if (timestampFrom > 0) {
		 	timestampOrDefault = timestampFrom * 1;
		} else {
			timestampOrDefault = Date.now();
		}

		const arr = await Promise.all(
			sourcesOrDefault.map(async (source) => {		
				let query = {};
				query.source = source;
				query.createdAt = { $lte: new Date(timestampOrDefault) };

				return await this.postsCollection
					.find(query)
					.sort({ _id: -1, createdAt: -1 })
					.limit(countPerPage + 1) 
					.skip(skipCount)
					.toArray();
			})
		);

		const filtered = arr.filter(item => item.length > countPerPage);
		let isNextAllowed = false;
		if (filtered.length > 0) {
			isNextAllowed = true;
		}

		let finalArray = [];
		for (let i = 0; i < countPerPage; i++) {
			for (let posts of arr) {
				const post = posts[i];
				if(!(typeof post === 'undefined')) {
				    finalArray.push(post);
				}
			}
		}

		return {
			posts: finalArray,
			isNextAllowed: isNextAllowed,
			page: pageOrDefault,
			timestamp: timestampOrDefault,
		};
	} 

	async cleanOldRecords() {
		const deleteAfterCount = 5000
		const records = await this.postsCollection
			.find()
			.sort({ _id: -1, createdAt : -1 })
			.limit(deleteAfterCount)
			.toArray();

		if (records.length < deleteAfterCount) {
			console.log(`Удалять нечего, записей меньше, чем ${deleteAfterCount}: ${records.length}`);
			return;
		}

		const lastRecord = records[deleteAfterCount - 1];
		const result = await this.postsCollection.deleteMany({ _id : { $lt: lastRecord._id } });

		console.log(`Удалено записей: ${result.deletedCount}`);
	}

}

exports.create = (context) => new FeedRepository(context);