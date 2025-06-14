import axios from 'axios';
import * as github from '@actions/github';
import {IReviewer} from "./types";

const webhookUrl = process.env.DISCORD_WEBHOOK_URL!;

export async function sendDiscordMessage(reviewers: IReviewer[]) {
    const pr = github.context.payload.pull_request;
    if (!pr) {
        throw new Error('pull_request 정보가 없어 디스코드 메시지를 보낼 수 없습니다.');
    }

    const mentions = reviewers.map(r => r.discordMention).join(', ');

    const message = {
        content: `📣 리뷰 요청!\n리뷰어: ${mentions}\n제목: **${pr.title}**\n링크: ${pr.html_url}`,
    };

    await axios.post(webhookUrl, message);
}