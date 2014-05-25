require('init')
require('gcm')
require('vcm')
while 1 do
  print("our score", gcm.get_game_our_score())
  print("our sttate", gcm.get_game_state())
  print("vector of penalites: ", gcm.get_game_penalty())
  print("are we in penalty? ", gcm.in_penalty());
  print("one of the posts? ",  vcm.get_post_d1())
  print("the other post? ", vcm.get_post_d2());	
end
