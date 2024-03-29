package com.minfo.mgr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.minfo.dao.TagDAO;
import com.minfo.dao.UserDAO;
import com.minfo.model.Answer;
import com.minfo.model.StatsPair;
import com.minfo.model.Tag;
import com.minfo.model.User;
import com.minfo.model.UserPrefs;
import com.minfo.model.UserStats;

public class UserManager {
	private UserDAO userDAO;
	private TagDAO tagDAO;

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void updateUser(User user) {
		userDAO.updateUser(user);
	}

	public void addUser(User user) {
		userDAO.addUser(user);
	}

	public List<User> getUser() {
		return userDAO.getUser();
	}

	public User getUser(Long id) {
		User u = userDAO.getUser(id);
		// System.out.println("user:"+u);
		// System.out.println("answers:"+u.getUserAnswers());
		return userDAO.getUser(id);
	}

	public void removeUser(Long id) {
		userDAO.removeUser(id);
	}

	public User addRegisterUser() {
		User u = new User();
		userDAO.addUser(u);
		u.setUsername("u" + u.getId());
		u.setPassword("password");
		userDAO.updateUser(u);
		return u;
	}
	
	public Map<StatsPair,Integer> getUserStatsMap(Long userId) {
		
		Map<StatsPair, Integer> result = new HashMap<StatsPair, Integer>();
		User u = userDAO.getUser(userId);
		
		List<Answer> userAnswers = u.getUserAnswers();
		for (Answer a : userAnswers) {
			for (Tag t : a.getPool().getTags()) {
				StatsPair sp = new StatsPair(t.getName(),a.getAnswer());
				if (!result.containsKey(sp)) {
					result.put(sp, new Integer(0));
				}
				Integer count = result.get(sp);
				count++;
				result.put(sp, count);
			}
		}
		
		return result;
		
	}
	
	public List<UserPrefs> getUserPrefs(Long userId) {
		User u = userDAO.getUser(userId);
		ArrayList<UserPrefs> al = updatePrefs(u);
		return al;
	}
	
	
	public ArrayList<UserPrefs> updatePrefs(User user) {
		
		List<Answer> userAnswers = user.getUserAnswers();
		Map<String,Long> countForTag = new HashMap<String,Long>();
		
		List<Tag> allTags = tagDAO.getTag();
		
		for(Tag t:allTags) {
			countForTag.put(t.getName(), 1L);
		}
		
		for (Answer a : userAnswers) {
			for (Tag t : a.getPool().getTags()) {
				StatsPair sp = new StatsPair(t.getName(),a.getAnswer());
				Long count = countForTag.get(t.getName());
				if("TAK".equals(a.getAnswer())) {
					count++;
				}
				else
				{
					count--;
				}
				if(count==0) {
					count=1L;
				}
				countForTag.put(t.getName(), count);
			}
		}
		
		Double totalCount=0d;
		for(Tag t:allTags) {
			totalCount+=countForTag.get(t.getName());
		}
		ArrayList<UserPrefs> al = new ArrayList<UserPrefs>();
		for(Tag t:allTags) {
			UserPrefs up = new UserPrefs();
			up.setPart(new Double(countForTag.get(t.getName()))/totalCount);
			up.setTag(t);
			up.setUser(user);
			al.add(up);
		}
		userDAO.setPrefs(al);
		return al;
	}
	

	public ArrayList<UserStats> getUserStats(Long userId) {
		Map<String, UserStats> result = new HashMap<String, UserStats>();
		User u = userDAO.getUser(userId);
		List<Answer> userAnswers = u.getUserAnswers();
		int totalCount = 0;
		for (Answer a : userAnswers) {
			for (Tag t : a.getPool().getTags()) {
				if (!result.containsKey(t.getName())) {
					result.put(t.getName(), new UserStats());
				}
				UserStats userStats = result.get(t.getName());
				int count = userStats.getCount();
				if (a.getAnswer().equals("TAK")) {
					count++;
				} else {
					count--;
				}
				userStats.setCount(count);
			}
		}

		for (Entry<String, UserStats> e : result.entrySet()) {
			totalCount += Math.abs(e.getValue().getCount());
		}
		ArrayList<UserStats> temp = new ArrayList<UserStats>();
		if (totalCount > 0) {
			for (Entry<String, UserStats> e : result.entrySet()) {
				e.getValue().setPercent(
						Math.abs(new Double(e.getValue().getCount()).doubleValue()) /
								 new Double(totalCount).doubleValue());
				if (e.getValue().getCount() > 0) {
					e.getValue().setAnswer("TAK");
				} else if (e.getValue().getCount() < 0) {
					e.getValue().setAnswer("NIE");
				}
				e.getValue().setTag(e.getKey());
			}
			temp.addAll(result.values());
		}
		return temp;

	}

	public void setTagDAO(TagDAO tagDAO) {
		this.tagDAO = tagDAO;
	}
}
