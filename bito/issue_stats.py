#coding=utf-8
from redminelib import Redmine
import datetime
import re
from collections import Counter

redmine_url = 'http://180.167.101.46:28080'
api_key = '4499ebf6087c260c69b5789d67bd8013f75a16d7'

redmine = Redmine(redmine_url, key = api_key)

today = datetime.date.today()

team_motion = ["Jimmy Jiao", u"Hao Wang", u"Yang02 Liu", "yu jiang", "Weisong Yang", u"huiliang liu"]
team_iot = [u"Tianjun Wu", "Qixiang Mao", "kelly Ma"]
team_cc = ["Zhongqiang Ren", "lei yin", "Wenhui Ma", "Xiaokun Jiang", u"Bo Deng"]
team_slam = [u"Congbiao Wen", "jin cao", "Cassis Liu", "Longtan Wang", u"Pu Yang","Jin Dai", "Chaohui Gong"]
team_testing = ["Sai Yu", "Li Xu", "wen li", "Ruiyuan Tian"]
team_web = [u"Haibo Jiang", "dandan jiang", u"Na Zhang", u"Jie Wang"]
team_product = ["lei huang", u"Yan Zhang"]
team_project = ["zuwu jiang", u"Zhenlin Zhang", u"He Chen", u"Fengru Xie", u"libao hu"]
others = [u"Dingjiang Zhou", "Nick Faughey", "", "Ariana Keeling", "shengnan ji", "kai tang", "Kang Lu", "Michael Wiznitzer", u"Wenhao Dong", "Yue Dong", "peifeng wang", "Redmine Admin", "Puneet Singhal", "Anirudh Topiwala"]
bito = [team_motion,team_web,team_iot,team_cc,team_testing,team_slam]

d = {"motion":team_motion,
	"iot":team_iot,
	"cc":team_cc,
	"slam":team_slam,
	"testing":team_testing,
	"web":team_web,
	"project":team_project,
	"product":team_product,
	"others":others
	}
p = {}

for team in d:
	for person in d[team]:
		p[person] = team

def edit_date(day):
	result = ""
	#temp = day.split("-")
	temp = re.split("/|-",day)
	for item in temp:
		if len(item) == 1:
			tmp = "0" + item
			result += tmp
		else:
			result += item
	return int(result)

nodeadline = []
passdue = []

count = 0
for issue in redmine.issue.filter(tracker_id = 1):
	try:
		if str(issue.status) != "Pending" and edit_date(str(issue.due_date)) <= edit_date(str(today)) - 1:
			passdue.append(str(issue.id) + ',' + str(p[str(issue.assigned_to)])+':'+str(issue.assigned_to) + ',' + str(issue.due_date))
	except Exception as e:
		nodeadline.append(str(issue.id) + ',' + str(p[str(issue.author)])+':'+str(issue.author))
	#print (issue)
	count += 1
print (count)

nodeadline.sort(key=lambda x: x.split(',')[1])
passdue.sort(key=lambda x: x.split(',')[1])

count_passdue = []
for item in passdue:
	count_passdue.append(item.split(',')[1].split(':')[0])
d1 = Counter(count_passdue)

count_nodeadline = []
for item in nodeadline:
	count_nodeadline.append(item.split(',')[1].split(':')[0])
d2 = Counter(count_nodeadline)

with open('nodeadline_'+str(today)+'.csv','w') as f9:
	f9.write("Created on " + str(today) + '\n')
	f9.write("This file shows the poorly created issues with their authors-please check and fix them" + '\n')
	f9.write("Issue #" + ',' + "Author" + '\n')
	for item in nodeadline:
		f9.write(str(item) + '\n')

with open('passdue_'+str(today)+'.csv','w') as f8:
	f8.write("Created on " + str(today) + '\n')
	f8.write("This file shows the issues that have been passed the due date-please check and fix them" + '\n')
	f8.write("Issue #" + ',' + "Assignee" + ',' + "Due Date" + '\n')
	for item in passdue:
		f8.write(str(item) + '\n')

with open('total_stats_'+str(today)+'.csv','w') as f7:
	f7.write("Created on " + str(today) + '\n')
	f7.write("This file shows the total stats for for all groups" + '\n')
	f7.write("Department"+','+"Passdue Numbers"+','+"Nodeadline Numbers"+'\n')
	for item in set(p.values()):
		try:
			print str(d1[item]),str(d2[item])
			f7.write(item+','+str(d1[item])+','+str(d2[item])+'\n')
		except:
			try:
				print str(d1[item])
				f7.write(item+','+str(d1[item])+','+str(0)+'\n')
			except:
				try:
					print str(d2[item])
					f7.write(item+','+str(0)+','+str(d2[item])+'\n')
				except:
					f7.write(item+','+str(0)+','+str(0)+'\n')
		'''try:
			f7.write(item+','+str(d1[item])+','+str(d2[item])+'\n')
		except:
			f7.write(item+','+str(d1[item])+','+str(0)+'\n')'''
