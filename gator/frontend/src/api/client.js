const BASE = '/api';

export const getActiveUser = () => {
  try { return JSON.parse(localStorage.getItem('gator_user')); } catch { return null; }
};
export const setActiveUser = (user) => localStorage.setItem('gator_user', JSON.stringify(user));
export const clearActiveUser = () => localStorage.removeItem('gator_user');

const headers = (userId) => ({
  'Content-Type': 'application/json',
  ...(userId ? { 'X-User-Id': userId } : {}),
});

const handle = async (res) => {
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.message || String(res.status));
  }
  if (res.status === 204) return null;
  return res.json();
};

export const api = {
  users: {
    create: (name) =>
      fetch(BASE + '/users', { method: 'POST', headers: headers(), body: JSON.stringify({ name }) }).then(handle),
    list: () =>
      fetch(BASE + '/users', { headers: headers() }).then(handle),
  },
  feeds: {
    list: (userId) =>
      fetch(BASE + '/feeds', { headers: headers(userId) }).then(handle),
    create: (userId, name, url) =>
      fetch(BASE + '/feeds', {
        method: 'POST', headers: headers(userId),
        body: JSON.stringify({ name, url }),
      }).then(handle),
    follow: (userId, feedId) =>
      fetch(BASE + '/feeds/' + feedId + '/follow', { method: 'POST', headers: headers(userId) }).then(handle),
    unfollow: (userId, feedId) =>
      fetch(BASE + '/feeds/' + feedId + '/follow', { method: 'DELETE', headers: headers(userId) }).then(handle),
    following: (userId) =>
      fetch(BASE + '/feeds/following', { headers: headers(userId) }).then(handle),
  },
  posts: {
    myFeed: (userId, page = 0, size = 20) =>
      fetch(BASE + '/posts?page=' + page + '&size=' + size, { headers: headers(userId) }).then(handle),
  },
};
