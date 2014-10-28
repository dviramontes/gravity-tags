var height, main, url, width, withData, xoffset, yoffset;

url = 'http://feeds.delicious.com/v2/json/tags/dviramontes';

width = 680;

height = 500;

xoffset = width / 2;

yoffset = height / 2;

withData = function(data) {
  return main(data);
};

$.ajax(url, {
  crossDomain: true,
  dataType: 'jsonp'
}).error(function(xhr, status, error) {
  return alert(error.message);
}).success(withData);

main = function(data) {
  var centerSolarSystem, group, star, svg, t0, tags, theme;
  tags = _.map(data, function(d, i) {
    return {
      tag: i,
      'count': d
    };
  });
  star = _.max(tags, 'count');
  centerSolarSystem = function(d, i) {
    if (d.tag === star.tag) {
      return 0;
    } else {
      return i + star.count;
    }
  };
  theme = d3.scale.linear().domain([1, tags.length]).range(['#67001f', '#ffffff', '#e0e0e0', '#b2182b', '#d6604d', '#f4a582', '#ffffe7', '#bababa', '#878787', '#4d4d4d', '#1a1a1a', '#fddbc7', '#ffff9f']);
  svg = d3.select('.viz').append('svg');
  svg.attr('width', width).attr('height', height).style({
    "border": '1px red solid'
  });
  group = svg.append('g').attr('class', 'solarSystem').attr('transform', 'translate(' + xoffset + ',' + yoffset + ')');
  t0 = Date.now();
  group.selectAll('circle').data(tags).enter().append('circle').attr('cx', centerSolarSystem).attr('cy', 0).attr('r', function(d, i) {
    return d.count;
  }).attr('fill', function(d, i) {
    return theme(i);
  });
  return d3.timer(function() {
    var delta, phi0, speed;
    phi0 = 45;
    speed = 0.001;
    delta = Date.now() - t0;
    d3.select('.solarSystem').selectAll('circle').attr("transform", function(d, i) {
      return "rotate(" + d.count + phi0 + delta * (speed * i) + ")";
    });
    return false;
  });
};
