package cn.od.bean;

public class UserFollow {
	private int follower_id;
	private int befollow_id;
	private int isFollow;
	
	@Override
	public String toString() {
		return "UseFollow [follower_id=" + follower_id + ", befollow_id=" + befollow_id + ", isFollow="
				+ isFollow + "]";
	}

	public int getfollower_id() {
		return follower_id;
	}

	public void setfollower_id(int follower_id) {
		this.follower_id = follower_id;
	}

	public int getbefollow_id() {
		return befollow_id;
	}

	public void setbefollow_id(int befollow_id) {
		this.befollow_id = befollow_id;
	}

	public int getIsFollow() {
		return isFollow;
	}

	public void setIsFollow(int isFollow) {
		this.isFollow = isFollow;
	}
	
	
	
	
}
